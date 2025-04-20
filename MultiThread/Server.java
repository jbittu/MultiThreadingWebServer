package MultiThread;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class Server {

    private final ExecutorService executor = Executors.newCachedThreadPool();

    public Consumer<Socket> getConsumer() {
        return (clientSocket) -> {
            System.out.println("Handling client: " + clientSocket.getRemoteSocketAddress());

            try (
                PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ) {
                toClient.println("Connected to server. Type 'exit' to quit.");

                String input;
                while ((input = fromClient.readLine()) != null) {
                    System.out.println("Client [" + clientSocket.getInetAddress() + "]: " + input);

                    if ("exit".equalsIgnoreCase(input)) {
                        toClient.println("Goodbye!");
                        break;
                    }

                    // Respond to client
                    toClient.println("Echo: " + input);
                }

            } catch (IOException e) {
                System.out.println("Connection error with client: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                    System.out.println("Connection closed with " + clientSocket.getRemoteSocketAddress());
                } catch (IOException e) {
                    System.out.println("Error closing socket: " + e.getMessage());
                }
            }
        };
    }

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(0); // Infinite timeout
            System.out.println(" Server is listening on port " + port + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(() -> getConsumer().accept(clientSocket));
            }

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        } finally {
            shutdownExecutor();
        }
    }

    private void shutdownExecutor() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }

    public static void main(String[] args) {
        int port = 8010;
        new Server().start(port);
    }
}
