/* eslint-disable @typescript-eslint/no-use-before-define */
/* eslint-disable no-console */
/* eslint-disable functional/no-let */
import { APIGatewayEvent, APIGatewayProxyResult } from "aws-lambda";
import * as dao from "./components/data_access_object";
import * as validator from "./components/validator";
import * as builder from "./components/response_builder";
import { HeaderWrapper, RequestBody } from "./types/definitions";
import { generateId } from "./components/utility";
import { MockResource } from "./model/mock_resource";
import { ExtractedData } from "./model/extracted_data";

export const getMockedResponse = async (
  request: APIGatewayEvent
): Promise<APIGatewayProxyResult> => {
  let response: APIGatewayProxyResult;
  try {
    const requestData = extractDataFromRequest(request);
    const mockResource = await readMockResourcesFromDB(requestData);
    const unmarshalledBody = validator.getUnmarshalledBody(
      requestData.body,
      requestData.contentType
    );
    const mockRule = validator.getValidMockRule(
      mockResource,
      request,
      unmarshalledBody
    );
    const mockResponse = validator.getMockResponse(
      mockRule,
      requestData.body,
      unmarshalledBody
    );
    response = builder.buildResponse(
      mockResponse.status,
      mockResponse.body,
      mockResponse.headers
    );
  } catch (error) {
    console.error(error);
    response = builder.buildErrorResponse(
      error,
      request.headers as HeaderWrapper
    );
  }
  return response;
};

const extractDataFromRequest = (request: APIGatewayEvent): ExtractedData => {
  const url = request.pathParameters?.proxy;
  const contentType =
    request.headers["Content-Type"] === undefined
      ? "text/html"
      : request.headers["Content-Type"];
  if (url === undefined) {
    throw new Error("No valid url found in path parameters");
  }
  const id = generateId(url, request.httpMethod);
  const body: RequestBody = request.body;
  return {
    body,
    contentType,
    id,
  };
};

const readMockResourcesFromDB = async (
  requestData: ExtractedData
): Promise<MockResource> => {
  const start = Date.now();
  const mockResource = await dao.readMockResource(requestData.id);
  console.debug(
    `The retrieving of mock response from database ended in [${
      Date.now() - start
    }] ms.\n`
  );
  return mockResource;
};
