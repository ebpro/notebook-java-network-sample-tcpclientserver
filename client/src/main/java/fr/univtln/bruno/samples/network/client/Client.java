package fr.univtln.bruno.samples.network.client;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * A simple client that sends a message to the server and logs the server's response.
 */
@Slf4j
public class Client {

    private final static int DEFAULT_SERVER_PORT = 55833;
    private final static String DEFAULT_SERVER_NAME = "localhost";

    /**
     * The main method that starts the client.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        int serverPort = getServerPort();
        String serverName = getServerName();
        String message = "Hello world";

        // Establish a connection to the server
        try (SocketChannel channel = SocketChannel.open(new InetSocketAddress(serverName, serverPort))) {
            // Send the message to the server
            ByteBuffer buffer = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
            channel.write(buffer);

            // Prepare to read the response from the server
            ByteBuffer responseBuffer = ByteBuffer.allocate(1024);
            channel.read(responseBuffer);
            responseBuffer.flip();

            // Convert the response to a string and log it
            String response = StandardCharsets.UTF_8.decode(responseBuffer).toString();
            log.info("Server response: {}", response);
        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }

    /**
     * Retrieves the server port from environment variables or uses the default port 55833.
     *
     * @return The server port.
     */
    private static int getServerPort() {
        return Optional.ofNullable(System.getenv("SERVER_PORT"))
                .filter(port -> !port.isEmpty())
                .map(Integer::parseInt)
                .orElse(DEFAULT_SERVER_PORT);
    }

    /**
     * Retrieves the server name from environment variables or uses the default name "localhost".
     *
     * @return The server name.
     */
    private static String getServerName() {
        return Optional.ofNullable(System.getenv("SERVER_NAME"))
                .filter(name -> !name.isEmpty())
                .orElse(DEFAULT_SERVER_NAME);
    }
}