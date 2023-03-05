/* eslint-disable functional/immutable-data */
/* eslint-disable fp/no-delete */
/* eslint-disable prefer-const */
/* eslint-disable functional/no-let */
/* eslint-disable one-var */
/* eslint-disable @typescript-eslint/no-explicit-any */

// eslint-disable-next-line @typescript-eslint/no-var-requires
// const xml2js = require("xml2js");
import * as xml2js from "xml2js";
const xml2jsOptions = {
  explicitArray: false,
  tagNameProcessors: [xml2js.processors.stripPrefix],
};

export const isNullOrUndefined = (obj: unknown | undefined): boolean =>
  obj === null || obj === undefined;

export const decodeBase64 = (data: string): string =>
  Buffer.from(data, "base64").toString("ascii");

export const encodeBase64 = (data: string): string =>
  Buffer.from(data, "ascii").toString("base64");

export const getBodyProperty = (bodyContent: any, propertyName: any): any => {
  if (!propertyName) {
    return bodyContent;
  }
  let bodyProperties = bodyContent;
  let analyzedProperty,
    properties = propertyName.split(".");
  let i = 0;
  let lenght = 0;
  for (i = 0, lenght = properties.length - 1; i < lenght; i++) {
    analyzedProperty = properties[i];
    let candidate = bodyProperties[analyzedProperty];
    if (candidate !== undefined) {
      bodyProperties = candidate;
    } else {
      break;
    }
  }
  return bodyProperties[properties[i]];
};

export const stringToXMLObject = (xmlStringContent: string): any => {
  let xmlUnmarshalledBody: any;
  xml2js.parseString(
    xmlStringContent,
    xml2jsOptions,
    (err: any, result: any) => {
      if (err) {
        // eslint-disable-next-line no-console
        console.log(err);
        return;
      }
      xmlUnmarshalledBody = result.Envelope.Body;
      if (result.$) {
        delete result.$;
      }
    }
  );
  return xmlUnmarshalledBody;
};

export const generateId = (resourceUrl: string, httpMethod: string): string => {
  const unescapedId = `${resourceUrl}${httpMethod}`;
  return unescapedId.replace(/[\\/\-_]+/g, "");
};
