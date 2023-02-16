/* eslint-disable no-console */
/* eslint-disable functional/immutable-data */
/* eslint-disable sort-keys */
/* eslint-disable @typescript-eslint/no-explicit-any */
// Import the express in typescript file
import * as express from "express";
import { APIGatewayProxyEvent } from "aws-lambda";
import * as dotenv from "dotenv";
import { getMockedResponse } from "./service";
import { decodeBase64, encodeBase64 } from "./components/utility";

import event = require("./static/apigateway_template.json");

// eslint-disable-next-line @typescript-eslint/no-var-requires
const bodyParser = require("body-parser");
dotenv.config({ path: "./.env" });

const port: number = 3002;
const app: express.Application = express();

// eslint-disable-next-line @typescript-eslint/explicit-function-return-type
const rawBodySaver = (req: any, res: any, buf: any, encoding: any) => {
  if (buf?.length) {
    req.body = buf.toString(encoding || "utf8");
  }
};

app.use(bodyParser.json({ verify: rawBodySaver }));
app.use(bodyParser.raw({ verify: rawBodySaver, type: "*/xml" }));
app.use(express.urlencoded({ extended: true }));

app.all("*", async (req, res) => {
  const contenttype = req.headers["content-type"];
  const ev = {
    ...event,
    body: encodeBase64(
      Object.keys(req.body).length === 0 ? "" : req.body.toString()
    ),
    httpMethod: req.method,
    pathParameters: {
      proxy: req.url,
    },
    headers: {
      "Content-Type": contenttype,
      SOAPAction: req.header("SOAPAction"),
    },
  };
  const start = Date.now();
  const response = await getMockedResponse(
    ev as unknown as APIGatewayProxyEvent
  );
  console.log(`Execution completed in [${Date.now() - start}] ms`);
  res.status(response.statusCode);
  const contentType = response.headers?.["Content-Type"] as string;
  if (contentType !== undefined) {
    res.setHeader("Content-type", contentType);
  }
  res.send(decodeBase64(response.body));
});


app.listen(port, () => {
  console.log(
    `PagoPA Mocker - Test server available at http://localhost:${port}/`
  );
});
