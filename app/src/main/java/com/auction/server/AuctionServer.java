package com.auction.server;

import java.net.ServerSocket;
import java.net.Socket;

import com.auction.shared.Auction;
import com.auction.shared.Electronics;
import com.auction.shared.Seller;

public class AuctionServer {
    private static final int PORT = 8080; // Su dung cong ma ban da chay thanh cong

    public static void main(String[] args) {
        System.out.println("Starting Auction Server on port " + PORT + "...");
        
        // --- KHOI TAO DU LIEU GIA LAP ---
        AuctionDAO dao = AuctionDAO.getInstance();
        Seller dummySeller = new Seller("S001", "nguoiban1", "pass123", "seller@mail.com");
        Electronics dummyItem = new Electronics("I001", "MacBook Pro", "Laptop cao cap", 1000.0, 
                                                System.currentTimeMillis(), System.currentTimeMillis() + 86400000, 
                                                "Apple", 12);
        Auction dummyAuction = new Auction("A001", dummyItem, dummySeller);
        dummyAuction.startAuction(); // Chuyen trang thai sang RUNNING
        dao.saveAuction(dummyAuction);
        System.out.println("Initialized dummy auction A001 (MacBook Pro) with starting price $1000.0");
        // ---------------------------------

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running and waiting for clients.");
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        } catch (Exception e) {
            System.err.println("Server exception: " + e.getMessage());
        }
    }
}