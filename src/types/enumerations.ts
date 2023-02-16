export enum ContentType {
  JSON = "JSON",
  XML = "XML",
  STRING = "STRING",
}

export enum HTTPMethod {
  GET = "GET",
  POST = "POST",
  PUT = "PUT",
  DELETE = "DELETE",
  PATCH = "PATCH",
  OPTIONS = "OPTIONS",
}

export enum RuleFieldPosition {
  BODY = "BODY",
  URL = "URL",
  HEADER = "HEADER",
}

export enum ConditionType {
  REGEX = "REGEX",
  EQ = "EQ",
  NEQ = "NEQ",
  LT = "LT",
  GT = "GT",
}
