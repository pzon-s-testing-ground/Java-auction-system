package com.auction.server;

import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        // Khoi tao Controller
        ServerController controller = new ServerController();

        try (
                java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(clientSocket.getInputStream())); java.io.PrintWriter out = new java.io.PrintWriter(clientSocket.getOutputStream(), true)) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received from client: " + inputLine);

                // Goi Controller de xu ly JSON va lay ket qua tra ve
                String jsonResponse = controller.processRequest(inputLine);

                // Gui ket qua ve cho Client
                out.println(jsonResponse);
            }
        } catch (java.io.IOException e) {
            System.err.println("Communication error with client: " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (java.io.IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }
}
