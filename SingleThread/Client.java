package SingleThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public void run() throws IOException {
        int port = 8081;
        InetAddress address = InetAddress.getByName("localhost");
        
        try (
            Socket socket = new Socket(address, port);
            PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        ) {
            System.out.println("Connected to server. Type messages or 'exit' to quit.");

            String serverMessage;
            String userMessage;

        
            if ((serverMessage = fromServer.readLine()) != null) {
                System.out.println("Server: " + serverMessage);
            }

            while (true) {
                System.out.print("client: ");
                userMessage = userInput.readLine();
                toServer.println(userMessage);

                if (userMessage.equalsIgnoreCase("exit")) {
                    break;
                }

                serverMessage = fromServer.readLine();
                if (serverMessage == null) break;
                System.out.println("Server: " + serverMessage);
            }

        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Client shutting down.");
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.run();
    }
}
