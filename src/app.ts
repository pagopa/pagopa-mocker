import { Context, APIGatewayProxyResult, APIGatewayEvent } from "aws-lambda";

/* eslint-disable prefer-arrow/prefer-arrow-functions */
export const handler = async function (
  _event: APIGatewayEvent,
  _context: Context
): Promise<APIGatewayProxyResult> {
  return {
    body: JSON.stringify({ message: "Hi there! I am a Lambda function!" }),
    headers: { "Content-Type": "application/json" },
    statusCode: 200,
  };
};
