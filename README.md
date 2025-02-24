# TCP Client-Server Sample Application

A Java multi-module project demonstrating TCP client-server communication using Java NIO channels and modern Java features.

## Project Structure

The project consists of three Maven modules:
- client - TCP client module
- server - TCP server module
- `parent` (root) - Parent module containing common dependencies and build configuration

### Key Features

- Modular Java application (Java Platform Module System)
- Custom runtime images using jlink
- Multi-module Maven project structure
- Java 21 features including virtual threads
- Non-blocking I/O using NIO channels

## Building the Project

### Standard Build
```sh
./mvnw clean package
```

### Creating Custom Runtime Images
The project uses jlink to create optimized, minimal runtime images:

```sh
./mvnw clean package -P jlink -pl client,server
```

We don't need to include the parent module in the build as it doesn't contain any application code and it doesn't support the jlink profile.

This creates custom runtime images for both client and server modules containing:
- Custom JRE with only required modules
- Application code and dependencies
- Platform-specific launchers

The runtime images can be found in:
- `client/target/maven-jlink/classifiers/runtime-image/`
- `server/target/maven-jlink/classifiers/runtime-image/`

## Module Information

- `fr.univtln.bruno.samples.network.client` - Client module
- `fr.univtln.bruno.samples.network.server` - Server module

## Running

### Using Maven
```sh
# Start server
./mvnw -pl server exec:java

# Start client
./mvnw -pl client exec:java
```

### Using Runtime Images
```sh
# Start server
./server/target/maven-jlink/classifiers/runtime-image/bin/app

# Start client
./client/target/maven-jlink/classifiers/runtime-image/bin/app
```

## Docker Support

The project includes Dockerfiles for both modules and a compose.yml for easy deployment:

```sh
docker compose up --build
```

## Dependencies

- JUnit 5 - Testing
- SLF4J/Logback - Logging
- Lombok - Boilerplate reduction
- Maven JLink Plugin - Custom runtime image creation

## License

Apache License 2.0