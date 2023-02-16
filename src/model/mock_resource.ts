/* eslint-disable functional/prefer-readonly-type */
import { HTTPMethod } from "../types/enumerations";
import { MockRule } from "./mock_rule";

export class MockResource {
  public id: string;
  public mockType: string;
  public resourceUrl: string;
  public httpMethod: string;
  public name: HTTPMethod;
  public tag: string[];
  public rules: MockRule[];
}
