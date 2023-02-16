/* eslint-disable functional/prefer-readonly-type */
import { HeaderWrapper } from "../types/definitions";

export class MockResponse {
  public id: string;
  public body: string;
  public parameters: string[];
  public status: number;
  public headers: HeaderWrapper;
}
