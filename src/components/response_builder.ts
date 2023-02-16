/* eslint-disable @typescript-eslint/no-use-before-define */
/* eslint-disable functional/no-let */
/* eslint-disable object-shorthand */
import { APIGatewayProxyResult } from "aws-lambda";
import { HeaderWrapper } from "../types/definitions";
import { encodeBase64 } from "./utility";

export const buildErrorResponse = (
  error: Error,
  headers: HeaderWrapper
): APIGatewayProxyResult => {
  // eslint-disable-next-line no-console
  console.error(`An error occurred during mock retrieving: ${error}`);
  const responseType = headers?.["Content-Type"] as string;
  const body = getDefaultMessageByContentType(error.message, responseType);
  return buildResponse(200, body, headers);
};

const getDefaultMessageByContentType = (
  message: string,
  responseType: string | undefined
): string => {
  let body = message;
  if (responseType === "application/json") {
    body = `{ "message": "${message}" }`;
  }
  // eslint-disable-next-line prettier/prettier
  else if (responseType === "text/xml" || responseType === "application/xml") {
    body = `<response><outcome>KO</outcome><message>${message}</message></response>`;
  }
  return encodeBase64(body);
};

export const buildResponse = (
  statusCode: number,
  message: string,
  headers: HeaderWrapper
): APIGatewayProxyResult => ({
  body: message,
  headers: headers,
  statusCode: statusCode,
});
