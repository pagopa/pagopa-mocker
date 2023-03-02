import { RequestBody } from "../types/definitions";

/* eslint-disable functional/prefer-readonly-type */
export class ExtractedData {
  public body: RequestBody;
  public contentType: string;
  public id: string;
}
