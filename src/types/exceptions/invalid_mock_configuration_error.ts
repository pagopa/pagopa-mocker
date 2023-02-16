export class InvalidMockConfigurationError extends Error {
  constructor(cause: string, e?: Error) {
    super();
    this.name = "InvalidMockConfigurationError";
    this.message = `The mock resource is not correctly configured. Cause: ${cause}`;
    this.stack = e?.stack;
  }
}
