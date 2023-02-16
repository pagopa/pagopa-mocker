export class NoMockResourceFoundError extends Error {
  constructor(id: string, e?: Error) {
    super();
    this.name = "NoMockResourceFound";
    this.message = `No mock resource found with id [${id}].`;
    this.stack = e?.stack;
  }
}
