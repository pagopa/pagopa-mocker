/* eslint-disable functional/prefer-readonly-type */
import { MockCondition } from "./mock_condition";
import { MockResponse } from "./mock_response";

export class MockRule {
  public id: string;
  public name: string;
  public tag: string[];
  public isActive: boolean;
  public conditions: MockCondition[];
  public response: MockResponse;
}
