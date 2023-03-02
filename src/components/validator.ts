/* eslint-disable no-console */
/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/no-use-before-define */
/* eslint-disable functional/no-let */
import { APIGatewayEvent } from "aws-lambda";
import { MockCondition } from "../model/mock_condition";
import { MockResource } from "../model/mock_resource";
import { MockResponse } from "../model/mock_response";
import { MockRule } from "../model/mock_rule";
import { RequestBody } from "../types/definitions";
import {
  ConditionType,
  ContentType,
  RuleFieldPosition,
} from "../types/enumerations";
import { InvalidMockConfigurationError } from "../types/exceptions/invalid_mock_configuration_error";
import { NotCompliantRequestError } from "../types/exceptions/not_compliant_request_error";
import * as utility from "./utility";
import { decodeBase64, getBodyProperty, isNullOrUndefined } from "./utility";

export const getValidMockRule = (
  mockResource: MockResource,
  request: APIGatewayEvent,
  unmarshalledBody: any
): MockRule => {
  const mockRules = mockResource.rules;
  const numberOfMockRules = mockRules.length;
  let result: MockRule | undefined;
  let ruleIndex = 0;
  let isRuleFound = false;
  while (canContinueRuleValidation(isRuleFound, ruleIndex, numberOfMockRules)) {
    const mockRule = mockRules[ruleIndex];
    if (mockRule.isActive) {
      console.debug(
        `Analyzing the mock rule [${mockRule.id}: ${mockRule.name}]`
      );
      const mockConditions = mockRule.conditions;
      const numberOfMockConditions = mockConditions.length;
      let isRuleStillValid = true;
      let conditionIndex = 0;
      // eslint-disable-next-line prettier/prettier
      while (canContinueValidationForRemainingConditions(isRuleStillValid, conditionIndex, numberOfMockConditions)) {
        const mockCondition = mockConditions[conditionIndex];
        isRuleStillValid = evaluateMockCondition(
          mockCondition,
          request,
          unmarshalledBody
        );
        conditionIndex++;
      }
      if (isRuleStillValid) {
        result = mockRule;
        isRuleFound = true;
      }
    }
    ruleIndex++;
  }
  if (result === undefined) {
    throw new NotCompliantRequestError(mockResource.id);
  }
  return result;
};

const evaluateMockCondition = (
  mockCondition: MockCondition,
  request: APIGatewayEvent,
  unmarshalledBody: any
): boolean => {
  let isValid = true;
  switch (mockCondition.fieldPosition) {
    case RuleFieldPosition.BODY:
      isValid = isBodyRequestSatisfyingRequirements(
        mockCondition,
        unmarshalledBody
      );
      break;
    case RuleFieldPosition.URL:
      isValid = isURLSatisfyingRequirements(mockCondition, request);
      break;
    case RuleFieldPosition.HEADER:
      isValid = areHeadersSatisfyingRequirements(mockCondition, request);
      break;
    default:
      break;
  }
  return isValid;
};

const canContinueRuleValidation = (
  isRuleFound: boolean,
  ruleIndex: number,
  numberOfMockRules: number
): boolean => !isRuleFound && ruleIndex < numberOfMockRules;

const canContinueValidationForRemainingConditions = (
  isRuleStillValid: boolean,
  conditionIndex: number,
  numberOfMockConditions: number
): boolean => isRuleStillValid && conditionIndex < numberOfMockConditions;

const isBodyRequestSatisfyingRequirements = (
  mockCondition: MockCondition,
  unmarshalledBody: any
): boolean => {
  console.debug(
    `Evaluating the mock condition: [${mockCondition.id}: ${mockCondition.fieldName} ${mockCondition.conditionType} ${mockCondition.conditionValue}].`
  );
  let isValid = false;
  if (!isNullOrUndefined(unmarshalledBody)) {
    switch (mockCondition.analyzedContentType) {
      case ContentType.JSON:
      case ContentType.XML:
        isValid = isFieldCompliantToCondition(mockCondition, unmarshalledBody);
        break;
      case ContentType.STRING:
        isValid = isContentCompliantToCondition(
          unmarshalledBody,
          mockCondition.conditionValue,
          mockCondition.conditionType
        );
        break;
      default:
        throw new InvalidMockConfigurationError(
          `No valid 'conditionType' field defined in the mock condition with id=${mockCondition.id}.`
        );
    }
  }
  return isValid;
};

const isURLSatisfyingRequirements = (
  _mockCondition: MockCondition,
  _request: APIGatewayEvent
): boolean => true;

const areHeadersSatisfyingRequirements = (
  mockCondition: MockCondition,
  request: APIGatewayEvent
): boolean => {
  const conditionType = mockCondition.conditionType;
  const conditionValue = mockCondition.conditionValue;
  const headerValue = request.headers?.[mockCondition.fieldName];
  console.debug(
    `Evaluating the mock condition for header: [${mockCondition.id}: ${headerValue} ${conditionType} ${conditionValue}].`
  );
  return headerValue !== undefined
    ? isContentCompliantToCondition(headerValue, conditionValue, conditionType)
    : false;
};

const isFieldCompliantToCondition = (
  mockCondition: MockCondition,
  body: any
): boolean => {
  const bodyFieldValue = utility.getBodyProperty(body, mockCondition.fieldName);
  const conditionType = mockCondition.conditionType;
  const conditionValue = mockCondition.conditionValue;
  return isContentCompliantToCondition(
    bodyFieldValue,
    conditionValue,
    conditionType
  );
};

const isContentCompliantToCondition = (
  fieldValue: any,
  conditionValue: string,
  conditionType: ConditionType
): boolean => {
  let isValid = false;
  if (fieldValue !== undefined) {
    switch (conditionType) {
      case ConditionType.REGEX:
        isValid = new RegExp(conditionValue, "g").test(fieldValue);
        break;
      case ConditionType.EQ:
        if (typeof fieldValue === "number") {
          isValid = Number(fieldValue) === Number(conditionValue);
        } else {
          isValid = fieldValue === conditionValue;
        }
        break;
      case ConditionType.NEQ:
        if (typeof fieldValue === "number") {
          isValid = Number(fieldValue) !== Number(conditionValue);
        } else {
          isValid = fieldValue !== conditionValue;
        }
        break;
      case ConditionType.GT:
        isValid = Number(fieldValue) > Number(conditionValue);
        break;
      case ConditionType.LT:
        isValid = Number(fieldValue) < Number(conditionValue);
        break;
      default:
        break;
    }
  }
  console.debug(
    `Evaluated the mock condition for value [${fieldValue}]. Is compliant to this condition= ${isValid}.`
  );
  return isValid;
};

export const getUnmarshalledBody = (
  body: RequestBody,
  contentType: string
): any => {
  let unmarshalledBody: any;
  if (body !== null && body !== undefined && body !== "") {
    switch (contentType) {
      case "application/json":
        unmarshalledBody = JSON.parse(utility.decodeBase64(body));
        break;
      case "text/xml":
      case "application/xml":
        unmarshalledBody = utility.stringToXMLObject(
          utility.decodeBase64(body)
        );
        break;
      default:
        unmarshalledBody = body;
    }
  }
  console.debug(
    `The request body extracted and unmarshalled as [${contentType}] content type is the following: [${unmarshalledBody}].`
  );
  return unmarshalledBody;
};

export const getMockResponse = (
  mockRule: MockRule,
  body: RequestBody,
  unmarshalledRequestBody: any
): MockResponse => {
  const parameters = mockRule.response.parameters;
  let decodedBody = decodeBase64(mockRule.response.body);
  parameters?.forEach((parameter) => {
    const placeholder = new RegExp(
      "\\$\\{" + `${parameter.replace(/\./g, "\\.")}` + "\\}",
      "g"
    );
    const parameterValue = getBodyProperty(unmarshalledRequestBody, parameter);
    decodedBody = decodedBody.replace(
      placeholder,
      parameterValue === undefined ? "" : parameterValue
    );
  });
  console.debug(
    `Extracted the following request body from mock rule: [${decodedBody}].`
  );
  return {
    ...mockRule.response,
    body: decodedBody,
  };
};
