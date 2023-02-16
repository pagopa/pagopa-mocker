import { Context, APIGatewayProxyResult, APIGatewayEvent } from "aws-lambda";
import { getMockedResponse } from "./service";

export const handler = async (
  event: APIGatewayEvent,
  _context: Context
): Promise<APIGatewayProxyResult> => getMockedResponse(event);
