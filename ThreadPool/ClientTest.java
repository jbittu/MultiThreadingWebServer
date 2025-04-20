package ThreadPool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientTest {

    private static final int PORT = 8010;
    private static final String HOST = "localhost";
    private static final int CLIENT_COUNT = 1000;

    public static void main(String[] args) {
        ExecutorService clientPool = Executors.newFixedThreadPool(100); // Adjust based on your CPU

        for (int i = 1; i <= CLIENT_COUNT; i++) {
            final int clientId = i + 1;
            clientPool.execute(() -> {
                try (Socket socket = new Socket(InetAddress.getByName(HOST), PORT);
                     PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    System.out.println("Client #" + clientId + " connected to server.");

                    // Read server greeting
                    String serverMessage = fromServer.readLine();
                    System.out.println("Client #" + clientId + " received: " + serverMessage);

                    // Send a message (optional)
                    toServer.println("Hello from client #" + clientId);

                    // Read echo or response (optional)
                    String reply = fromServer.readLine();
                    System.out.println("Client #" + clientId + " got reply: " + reply);

                    // Exit message
                    toServer.println("exit");

                } catch (IOException e) {
                    System.err.println("Client #" + clientId + " failed: " + e.getMessage());
                }
            });
        }

        clientPool.shutdown();
    }
}

