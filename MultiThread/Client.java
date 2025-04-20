package MultiThread;
import java.io.*;
import java.net.*;

public class Client {

    private int clientId;

    public Client(int clientId) {
        this.clientId = clientId;
    }

    public Runnable getRunnable() {
        return () -> {
            int port = 8010;
            try (
                Socket socket = new Socket(InetAddress.getByName("localhost"), port);
                PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()))
            ) {
                System.out.println("Client #" + clientId + " connected from " + socket.getLocalSocketAddress());

                // Send messages to server
                toServer.println("Hello from Client #" + clientId);
                toServer.println("This is a test message.");
                toServer.println("exit"); // Trigger server to close connection

                // Read responses from server
                String response;
                while ((response = fromServer.readLine()) != null) {
                    System.out.println("Client #" + clientId + " got: " + response);
                    if (response.toLowerCase().contains("goodbye")) break;
                }

            } catch (IOException e) {
                System.out.println("Client #" + clientId + " error: " + e.getMessage());
            }
        };
    }

    public static void main(String[] args) {
        int clientCount = 100;
        for (int i = 0; i < clientCount; i++) {
            Client client = new Client(i + 1);
            Thread thread = new Thread(client.getRunnable());
            thread.start();

            // Optional: slight delay to avoid overwhelming the server all at once
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {}
        }
    }
}
