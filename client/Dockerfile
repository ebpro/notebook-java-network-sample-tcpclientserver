# Multi-stage build for Java application with jlink using Maven profile
# Stage 1: Build environment
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy Maven wrapper files first
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Make Maven wrapper executable and install build dependencies
RUN chmod +x mvnw && \
    apk add --no-cache bash

# Copy source code
COPY ./src ./src
COPY ./client ./client
COPY ./server ./server

# Download dependencies and build with jlink profile
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw clean -pl client -P jlink package

# Stage 2: Runtime image
FROM alpine:3.19

# Install minimal runtime dependencies
RUN apk add --no-cache \
    busybox-extras \
    net-tools \
    tzdata \
    && addgroup -S appgroup \
    && adduser -S appuser -G appgroup

# Copy the custom JRE and application
COPY --from=builder /app/client/target/maven-jlink/classifiers/runtime-image/ /jre
COPY --from=builder /app/client/target/*.jar /app/myapp.jar

# Set permissions
RUN chown -R appuser:appgroup /app /jre && \
    chmod -R g=u /app

# Configure environment variables
ENV SERVER_NAME=server \
    SERVER_PORT=55833 \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

USER appuser

# Use module path and module name
ENTRYPOINT ["/jre/bin/java", "-jar", "/app/myapp.jar"]