#
# Build stage
#
FROM --platform=amd64 maven:3.8.4-jdk-11-slim as mvnbuild
WORKDIR /build
COPY . .
RUN mvn clean package -Dmaven.test.skip=true

#
# Package stage
#
FROM --platform=amd64 adoptopenjdk/openjdk11:alpine-jre as bootimg
COPY --from=mvnbuild /build/target/*.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

#
# AppInsight installation stage
#
FROM --platform=amd64 ghcr.io/pagopa/docker-base-springboot-openjdk11:v1.0.1@sha256:bbbe948e91efa0a3e66d8f308047ec255f64898e7f9250bdb63985efd3a95dbf
COPY --chown=spring:spring  --from=bootimg dependencies/ ./
COPY --chown=spring:spring  --from=bootimg snapshot-dependencies/ ./
# https://github.com/moby/moby/issues/37965#issuecomment-426853382
RUN true
COPY --chown=spring:spring  --from=bootimg spring-boot-loader/ ./
COPY --chown=spring:spring  --from=bootimg application/ ./

EXPOSE 8080



