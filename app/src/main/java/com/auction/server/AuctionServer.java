package com.auction.server;

import java.net.ServerSocket;
import java.net.Socket;

public class AuctionServer {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        System.out.println("Starting Auction Server on port " + PORT + "...");
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running and waiting for clients.");
            
            while (true) {
                // Chap nhan ket noi tu Client
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                
                // Tao mot luong moi de xu ly Client nay
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        } catch (Exception e) {
            System.out.println("Server exception: " + e.getMessage());
            // e.printStackTrace();
        }
    }
}