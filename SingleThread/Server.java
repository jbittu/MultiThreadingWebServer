package SingleThread;

import java.io.*;
import java.net.*;

public class Server {
    public void run() {
        int port = 8081;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(10000);
            System.out.println("Server started on port " + port);

            while (true) {
                try {
                    Socket acceptedConnection = serverSocket.accept();
                    System.out.println("Accepted connection from " + acceptedConnection.getRemoteSocketAddress());

                    PrintWriter toClient = new PrintWriter(acceptedConnection.getOutputStream(), true);
                    BufferedReader fromClient = new BufferedReader(new InputStreamReader(acceptedConnection.getInputStream()));
                    BufferedReader serverInput = new BufferedReader(new InputStreamReader(System.in));

                    // Initial greeting
                    toClient.println("Hello from server! Type 'exit' to quit.");
                    System.out.println("Type your messages below:");

                    String clientMsg, serverMsg;

                    while ((clientMsg = fromClient.readLine()) != null) {
                        System.out.println("Client: " + clientMsg);

                        if (clientMsg.equalsIgnoreCase("exit")) {
                            toClient.println("Goodbye!");
                            break;
                        }

                        // Let server type a response
                        System.out.print("server: ");
                        serverMsg = serverInput.readLine();
                        toClient.println(serverMsg);

                        if (serverMsg.equalsIgnoreCase("exit")) {
                            System.out.println("Connection ending...");
                            break;
                        }
                    }

                    // Clean up
                    fromClient.close();
                    toClient.close();
                    acceptedConnection.close();
                    System.out.println("Connection closed.");

                } catch (IOException e) {
                    System.out.println("Timeout or connection error: " + e.getMessage());
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            System.out.println("Server shutting down.");
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.run();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
