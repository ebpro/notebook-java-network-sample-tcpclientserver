# Multi-stage build for Java application with jlink using Maven profile
# Stage 1: Build environment
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

# Copy Maven wrapper files first
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Make Maven wrapper executable
RUN chmod +x mvnw

# Copy source code
COPY ./src ./src
COPY ./server ./server
COPY ./client ./client

# Download dependencies and build with jlink profile
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw clean -pl server -P jlink package

# Stage 2: Runtime image
FROM ubuntu:jammy

# Install net-tools to get netstat
RUN apt-get update && apt-get install -y net-tools && rm -rf /var/lib/apt/lists/*

# Copy the custom JRE and application
COPY --from=builder /app/server/target/maven-jlink/classifiers/runtime-image/ /jre
COPY --from=builder /app/server/target/*.jar /app/myapp.jar

# Create non-root user and set permissions
RUN useradd -r -u 1001 -g root appuser && \
    chown -R appuser:root /app && \
    chmod -R g=u /app

USER appuser

# Configure Java options for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Use module path and module name
ENTRYPOINT ["/jre/bin/java", "-jar", "/app/myapp.jar"]