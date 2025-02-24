# Stage 1: Build environment
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy Maven wrapper files
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Make Maven wrapper executable and install build dependencies
RUN chmod +x mvnw && \
    apk add --no-cache bash

# Copy source code
COPY ./src ./src
COPY ./server ./server
COPY ./client ./client

# Build with jlink profile
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw clean -pl server -P jlink package

# Stage 2: Runtime image
FROM alpine:3.19

# Install minimal runtime dependencies including networking tools
RUN apk add --no-cache \
    busybox-extras \
    net-tools \
    tzdata \
    && addgroup -S appgroup \
    && adduser -S appuser -G appgroup

# Copy the custom JRE and application
COPY --from=builder /app/server/target/maven-jlink/classifiers/runtime-image/ /jre
COPY --from=builder /app/server/target/*.jar /app/myapp.jar

# Set permissions
RUN chown -R appuser:appgroup /app /jre && \
    chmod -R g=u /app

USER appuser

# Configure Java options for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Use module path and module name
ENTRYPOINT ["/jre/bin/java", "-jar", "/app/myapp.jar"]