package com.auction.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received from client: " + inputLine);
                String jsonResponse = "{\"status\":\"SUCCESS\", \"message\":\"Server received your data\"}";
                out.println(jsonResponse);
            }
        } catch (java.io.IOException e) { // Sua Exception thanh IOException
            System.err.println("Communication error with client: " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (java.io.IOException e) { // Bắt ngoại lệ cụ thể khi đóng kết nối
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }
}