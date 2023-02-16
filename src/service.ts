/* eslint-disable no-console */
/* eslint-disable functional/no-let */
import { APIGatewayEvent, APIGatewayProxyResult } from "aws-lambda";
import * as dao from "./components/data_access_object";
import * as validator from "./components/validator";
import * as builder from "./components/response_builder";
import { HeaderWrapper, RequestBody } from "./types/definitions";
import { generateId } from "./components/utility";

export const getMockedResponse = async (
  request: APIGatewayEvent
): Promise<APIGatewayProxyResult> => {
  let response: APIGatewayProxyResult;
  let dbExecutionTime = 0;
  try {
    const url = request.pathParameters?.proxy;
    const contentType = request.headers["Content-Type"];
    if (url === undefined) {
      throw new Error("No valid url found in path parameters");
    }
    const id = generateId(url, request.httpMethod);
    const body: RequestBody = request.body;
    const start = Date.now();
    const mockResource = await dao.readMockResource(id);
    dbExecutionTime = Date.now() - start;
    const unmarshalledBody = validator.getUnmarshalledBody(
      body,
      contentType === undefined ? "text/html" : contentType
    );
    const mockRule = validator.getValidMockRule(
      mockResource,
      request,
      unmarshalledBody
    );
    const mockResponse = validator.getMockResponse(
      mockRule,
      body,
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
  // eslint-disable-next-line no-console
  console.debug(
    `The retrieving of mock response from database ended in [${dbExecutionTime}] ms.\n`
  );
  return response;
};
