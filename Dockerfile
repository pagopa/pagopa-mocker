FROM --platform=linux/amd64 public.ecr.aws/lambda/nodejs:16 as builder
WORKDIR /usr/app/
COPY package.json  ./
COPY tsconfig.json  ./

COPY src src/
RUN npm install
RUN npm install -g rimraf
RUN npm run build

FROM --platform=linux/amd64 public.ecr.aws/lambda/nodejs:16
WORKDIR ${LAMBDA_TASK_ROOT}
COPY --from=builder /usr/app/dist/. ./
COPY --from=builder /usr/app/node_modules/. ../node_modules/
COPY --from=builder /usr/app/node_modules/. ./
CMD ["app.handler"]