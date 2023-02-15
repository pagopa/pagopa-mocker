FROM --platform=linux/amd64 public.ecr.aws/lambda/nodejs:18 as builder
WORKDIR /usr/app/
COPY package.json  ./
COPY tsconfig.json  ./

COPY src src/
RUN npm install --legacy-peer-deps
RUN npm install -g rimraf
RUN npm run build

FROM --platform=linux/amd64 public.ecr.aws/lambda/nodejs:18
WORKDIR ${LAMBDA_TASK_ROOT}
COPY --from=builder /usr/app/dist/* ./
CMD ["app.handler"]