/* eslint-disable functional/prefer-readonly-type */
export type RequestBody = string | null;

export type HeaderWrapper =
  | { [header: string]: boolean | number | string }
  | undefined;
