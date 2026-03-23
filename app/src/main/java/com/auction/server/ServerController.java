package com.auction.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class ServerController {

    public String processRequest(String jsonRequest, java.io.PrintWriter clientOut) {
        try {
            JsonObject request = JsonParser.parseString(jsonRequest).getAsJsonObject();
            String action = request.get("action").getAsString();

            return switch (action) {
                case "SIGNUP" -> handleSignup(request);
                case "LOGIN" -> handleLogin(request);
                case "GET_AUCTIONS" -> handleGetAuctions();
                case "JOIN_AUCTION" -> handleJoinAuction(request, clientOut);
                case "PLACE_BID" -> handlePlaceBid(request);
                default -> "{\"status\":\"ERROR\", \"message\":\"Unknown action\"}";
            };
        } catch (JsonSyntaxException e) {
            return "{\"status\":\"ERROR\", \"message\":\"Invalid format\"}";
        }
    }

    private String handleSignup(JsonObject request) {
        String username = request.get("username").getAsString();
        String password = request.get("password").getAsString();
        boolean success = UserDAO.getInstance().registerUser(username, password);
        
        JsonObject response = new JsonObject();
        if (success) {
            response.addProperty("status", "SUCCESS");
            response.addProperty("message", "Account created successfully.");
        } else {
            response.addProperty("status", "FAILED");
            response.addProperty("message", "Username already exists.");
        }
        return response.toString();
    }

    private String handleLogin(JsonObject request) {
        String username = request.get("username").getAsString();
        String password = request.get("password").getAsString();
        boolean success = UserDAO.getInstance().authenticateUser(username, password);
        
        JsonObject response = new JsonObject();
        if (success) {
            response.addProperty("status", "SUCCESS");
            response.addProperty("message", "Login successful.");
            response.addProperty("username", username);
        } else {
            response.addProperty("status", "FAILED");
            response.addProperty("message", "Invalid username or password.");
        }
        return response.toString();
    }

    private String handleGetAuctions() {
        JsonObject response = new JsonObject();
        response.addProperty("status", "SUCCESS");
        response.add("auctions", AuctionDAO.getInstance().getAllAuctionsAsJson());
        return response.toString();
    }

    private String handleJoinAuction(JsonObject request, java.io.PrintWriter clientOut) {
        String auctionId = request.get("auctionId").getAsString();
        com.auction.shared.Auction auction = AuctionDAO.getInstance().getAuction(auctionId);
        JsonObject response = new JsonObject();

        if (auction != null) {
            // ĐIỂM QUAN TRỌNG 1: Ghi danh Client vao SessionManager
            SessionManager.joinRoom(auctionId, clientOut);

            response.addProperty("status", "SUCCESS");
            response.addProperty("message", "Joined auction " + auctionId);
            response.addProperty("itemName", auction.getItem().getName());
            response.addProperty("currentBid", auction.getItem().getCurrentHighestBid());
        } else {
            response.addProperty("status", "FAILED");
            response.addProperty("message", "Auction does not exist.");
        }
        return response.toString();
    }

    private String handlePlaceBid(JsonObject request) {
        String userId = request.get("userId").getAsString();
        String auctionId = request.get("auctionId").getAsString();
        double bidAmount = request.get("bidAmount").getAsDouble();

        AuctionDAO dao = AuctionDAO.getInstance();
        com.auction.shared.Auction auction = dao.getAuction(auctionId);
        JsonObject response = new JsonObject();

        if (auction == null) {
            response.addProperty("status", "FAILED");
            response.addProperty("message", "Auction not found");
            return response.toString();
        }

        com.auction.shared.Bidder bidder = new com.auction.shared.Bidder(userId, userId, "pass", "email");
        com.auction.shared.BidTransaction bid = new com.auction.shared.BidTransaction("TXN" + System.currentTimeMillis(), bidder, bidAmount);

        boolean success = auction.placeBid(bid);

        if (success) {
            dao.saveAuction(auction);
            
            // ĐIỂM QUAN TRỌNG 2: Phát sóng giá mới cho toàn bộ người trong phòng
            JsonObject broadcastMsg = new JsonObject();
            broadcastMsg.addProperty("action", "BROADCAST_NEW_BID");
            broadcastMsg.addProperty("newBid", auction.getItem().getCurrentHighestBid());
            broadcastMsg.addProperty("bidder", userId);
            SessionManager.broadcast(auctionId, broadcastMsg.toString());

            response.addProperty("status", "SUCCESS");
            response.addProperty("message", "Bid placed successfully.");
        } else {
            response.addProperty("status", "FAILED");
            response.addProperty("message", "Bid rejected.");
        }
        return response.toString();
    }
}