package com.auction.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.JsonObject;

public class AuctionClient {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Connected to Auction Server.");

            // Tao mot request JSON su dung Gson de dat gia
            JsonObject request = new JsonObject();
            request.addProperty("action", "PLACE_BID");
            request.addProperty("userId", "bidder123");
            request.addProperty("auctionId", "A001");
            request.addProperty("bidAmount", 1050.0); // Dat gia 1050 (cao hon gia khoi diem 1000)

            String jsonString = request.toString();
            System.out.println("Sending to server: " + jsonString);
            
            out.println(jsonString);

            // Doc phan hoi tu Server
            String response = in.readLine();
            System.out.println("Response from server: " + response);

        } catch (Exception e) {
            System.out.println("Client exception: " + e.getMessage());
            // e.printStackTrace();
        }
    }
}