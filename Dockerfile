# Multi-stage build for Java application with jlink using Maven profile
# Stage 1: Build environment
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy Maven wrapper files first
COPY .mvn/ .mvn/ 
COPY mvnw pom.xml ./

# Make Maven wrapper executable and install build dependencies
RUN chmod +x mvnw

# Copy source code
COPY ./client/ ./client/
COPY ./server/ ./server/

# Download dependencies and build with jlink profile
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw clean package -P jlink \
    -Dmaven.javadoc.skip=true \
    -Dmaven.source.skip=true \
    -pl server,client

# Stage 2: Runtime image
FROM alpine:3.19 AS base-runtime

# Install minimal runtime dependencies
RUN apk add --no-cache \
    busybox-extras \
    net-tools \
    tzdata \
    && addgroup -S appgroup \
    && adduser -S appuser -G appgroup

# Configure environment variables
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

USER appuser

# Use module path and module name
ENTRYPOINT ["./jre/bin/app"]

# Stage 3: Copy runtime image for client
FROM base-runtime AS client-runtime
COPY --from=builder --chown=appuser:appgroup \
    /app/client/target/maven-jlink/classifiers/runtime-image/ ./jre

# Stage 4: Copy runtime image for server
FROM base-runtime AS server-runtime

COPY --from=builder --chown=appuser:appgroup \
    /app/server/target/maven-jlink/classifiers/runtime-image/ ./jre

EXPOSE ${SERVER_PORT}

# HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
#        CMD netstat -ln | grep ${SERVER_PORT} || exit 1

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
        CMD nc -z ${SERVER_NAME} ${SERVER_PORT} || exit 1