package ThreadPool;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server {
    private final ExecutorService threadPool;

    public Server(int poolSize) {
        this.threadPool = Executors.newFixedThreadPool(poolSize);
    }

    public void handleClient(Socket clientSocket) {
        System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());

        try (
            PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            toClient.println(" Hello from server at " + InetAddress.getLocalHost().getHostAddress());

            String input;
            while ((input = fromClient.readLine()) != null) {
                System.out.println("Received from " + clientSocket.getRemoteSocketAddress() + ": " + input);

                if ("exit".equalsIgnoreCase(input)) {
                    toClient.println(" Goodbye!");
                    break;
                }

                // Echo back or process command
                toClient.println("Echo: " + input);
            }

        } catch (IOException ex) {
            System.out.println(" Connection error: " + ex.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println(" Disconnected: " + clientSocket.getRemoteSocketAddress());
            } catch (IOException e) {
                System.out.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(0); // Infinite wait
            System.out.println("🚀 Server is listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(() -> handleClient(clientSocket));
            }

        } catch (IOException ex) {
            System.out.println("Server error: " + ex.getMessage());
        } finally {
            shutdownThreadPool();
        }
    }

    private void shutdownThreadPool() {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
        }
    }

    public static void main(String[] args) {
        int port = 8010;
        int poolSize = 10;

        Server server = new Server(poolSize);
        server.start(port);
    }
}

