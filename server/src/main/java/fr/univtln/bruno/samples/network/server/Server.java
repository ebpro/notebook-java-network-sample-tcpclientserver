package fr.univtln.bruno.samples.network.server;

import lombok.extern.slf4j.Slf4j;

import java.nio.channels.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Executors;

/**
 * A simple server that converts received text to uppercase and sends it back to the client.
 */
@Slf4j
public class Server {
    private final static int DEFAULT_SERVER_PORT = 55833;

    /**
     * The main method that starts the server.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        int port = getPort();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor();
             var serverChannel = ServerSocketChannel.open()) {
            serverChannel.bind(new InetSocketAddress(port));
            log.info("Uppercase server started on port {}", port);

            while (!Thread.interrupted()) {
                var clientChannel = serverChannel.accept();
                executor.submit(() -> handleClient(clientChannel));
            }
        } catch (IOException e) {
            log.error("Server error: {}", e.getMessage());
        }
    }

    /**
     * Handles communication with a connected client.
     *
     * @param clientChannel The channel connected to the client.
     */
    private static void handleClient(SocketChannel clientChannel) {
        try (clientChannel) {
            var buffer = ByteBuffer.allocate(1024);
            var remoteAddress = clientChannel.getRemoteAddress();
            log.info("Client connected: {}", remoteAddress);

            while (clientChannel.read(buffer) != -1) {
                buffer.flip();
                var text = StandardCharsets.UTF_8.decode(buffer).toString().toUpperCase();
                var responseBuffer = ByteBuffer.wrap(text.getBytes(StandardCharsets.UTF_8));
                clientChannel.write(responseBuffer);
                buffer.clear();
            }
            log.info("Client disconnected: {}", remoteAddress);
        } catch (IOException e) {
            log.error("Client error: {}", e.getMessage());
        }
    }

    /**
     * Retrieves the server port from environment variables or uses the default port 55833.
     *
     * @return The server port.
     */
    private static int getPort() {
        return Optional.ofNullable(System.getenv("SERVER_PORT"))
                .filter(port -> !port.isEmpty())
                .map(Integer::parseInt)
                .orElse(DEFAULT_SERVER_PORT);
    }
}