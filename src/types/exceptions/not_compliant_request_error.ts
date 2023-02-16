export class NotCompliantRequestError extends Error {
  constructor(id: string, e?: Error) {
    super();
    this.name = "NotCompliantRequestError";
    this.message = `The passed request is not compliant with no rule for the found mock resource with id [${id}]`;
    this.stack = e?.stack;
  }
}
