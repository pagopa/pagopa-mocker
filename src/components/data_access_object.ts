/* eslint-disable functional/immutable-data */
/* eslint-disable functional/prefer-readonly-type */
/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable functional/no-let */
import { AWSError, DynamoDB } from "aws-sdk";
import { MockResource } from "../model/mock_resource";
import { NoMockResourceFoundError } from "../types/exceptions/no_mock_resource_found_error";

const TABLE_NAME = "pagopamockresource";
const LOCAL_DYNAMODB_ENDPOINT = "http://pagopamockdb:8000";

const getDocumentClientOptions = (): any => {
  let result;
  if (process.env.IN_LOCAL) {
    result = {
      endpoint: LOCAL_DYNAMODB_ENDPOINT,
      region: "local",
    };
  } else {
    result = {
      // endpoint: process.env.DYNAMODB_ENDPOINT,
      region: "eu-west-1", // process.env.DYNAMODB_REGION,
    };
  }
  return result;
};

const getTableName = (): string =>
  process.env.TABLE_NAME === undefined ? TABLE_NAME : process.env.TABLE_NAME;

const handleError = (err: AWSError): void => {
  if (err) {
    throw new Error(`An error occurred while read data. Error: ${err}`);
  }
};

export const readMockResource = async (
  resourceId: string
): Promise<MockResource> => {
  let result;
  try {
    const parameters = {
      Key: {
        id: resourceId,
      },
      TableName: getTableName(),
    };
    const database = new DynamoDB.DocumentClient(getDocumentClientOptions());
    result = await database.get(parameters).promise();
  } catch (err) {
    handleError(err);
  }
  const item = result?.Item as MockResource;
  if (item === undefined) {
    throw new NoMockResourceFoundError(resourceId);
  }
  return item;
};
