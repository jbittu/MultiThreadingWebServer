package ThreadPool;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    public void start(String host, int port) {
        try (
            Socket socket = new Socket(host, port);
            PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println(" Connected to server at " + socket.getRemoteSocketAddress());

            // Print server's greeting
            String serverResponse = fromServer.readLine();
            System.out.println(" Server: " + serverResponse);

            String input;
            while (true) {
                System.out.print(" You: ");
                input = scanner.nextLine();

                toServer.println(input); // send to server

                if ("exit".equalsIgnoreCase(input)) {
                    System.out.println(" Exiting client...");
                    break;
                }

                // Read and display server's response
                serverResponse = fromServer.readLine();
                if (serverResponse != null) {
                    System.out.println(" Server: " + serverResponse);
                } else {
                    System.out.println(" Server disconnected.");
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println(" Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String host = "localhost";
        int port = 8010;

        new Client().start(host, port);
    }
}

