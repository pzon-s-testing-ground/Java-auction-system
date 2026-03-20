package com.auction.server;

import com.auction.shared.Auction;
import com.auction.shared.BidTransaction;
import com.auction.shared.Bidder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ServerController {
    
    public String processRequest(String jsonRequest) {
        try {
            JsonObject request = JsonParser.parseString(jsonRequest).getAsJsonObject();
            String action = request.get("action").getAsString();

            // 1. Su dung Switch Expression cua Java hien dai
            return switch (action) {
                case "SIGNUP" -> handleSignup(request);
                case "LOGIN" -> handleLogin(request);
                case "JOIN_AUCTION" -> handleJoinAuction(request);
                case "PLACE_BID" -> handlePlaceBid(request);
                case "GET_AUCTIONS" -> handleGetAuctions();
                default -> "{\"status\":\"ERROR\", \"message\":\"Unknown action\"}";
            };
            
        } catch (com.google.gson.JsonSyntaxException | IllegalStateException | NullPointerException e) {
            // 2. Bat cac ngoai le cu the cua Gson thay vi Exception chung chung
            return "{\"status\":\"ERROR\", \"message\":\"Invalid JSON format\"}";
        }
    }

    private String handleJoinAuction(JsonObject request) {
        String userId = request.get("userId").getAsString();
        String auctionId = request.get("auctionId").getAsString();

        // Lay thong tin tu Database/Cache
        AuctionDAO dao = AuctionDAO.getInstance();
        com.auction.shared.Auction auction = dao.getAuction(auctionId);

        JsonObject response = new JsonObject();

        if (auction != null) {
            response.addProperty("status", "SUCCESS");
            response.addProperty("message", "User " + userId + " successfully joined auction " + auctionId);
            // Gui them thong tin thuc te cua san pham
            response.addProperty("itemName", auction.getItem().getName());
            response.addProperty("currentBid", auction.getItem().getCurrentHighestBid());
        } else {
            response.addProperty("status", "FAILED");
            response.addProperty("message", "Auction ID " + auctionId + " does not exist.");
        }
        
        return response.toString();
    }
    // LOGIC XU LY DAT GIA
    private String handlePlaceBid(JsonObject request) {
        String userId = request.get("userId").getAsString();
        String auctionId = request.get("auctionId").getAsString();
        double bidAmount = request.get("bidAmount").getAsDouble();

        AuctionDAO dao = AuctionDAO.getInstance();
        Auction auction = dao.getAuction(auctionId);

        JsonObject response = new JsonObject();

        if (auction == null) {
            response.addProperty("status", "ERROR");
            response.addProperty("message", "Auction not found");
            return response.toString();
        }

        // Tao mot Bidder tam thoi de dai dien cho nguoi dung hien tai
        Bidder bidder = new Bidder(userId, "BidderName", "pass", "email@test.com");
        BidTransaction bid = new BidTransaction("TXN" + System.currentTimeMillis(), bidder, bidAmount);

        // Goi logic dat gia da tich hop synchronized tu Tuan 1
        boolean success = auction.placeBid(bid);

        if (success) {
            dao.saveAuction(auction);

            response.addProperty("status", "SUCCESS");
            response.addProperty("message", "Bid placed successfully. Current highest bid is now $" + auction.getItem().getCurrentHighestBid());
        } else {
            response.addProperty("status", "FAILED");
            response.addProperty("message", "Bid rejected. Amount must be higher than current bid ($" + auction.getItem().getCurrentHighestBid() + ") and auction must be RUNNING.");
        }

        return response.toString();
    }

    // XU LY SIGNUP
    private String handleSignup(JsonObject request) {
        String username = request.get("username").getAsString();
        String password = request.get("password").getAsString();

        boolean success = UserDAO.getInstance().registerUser(username, password);
        JsonObject response = new JsonObject();
        
        if (success) {
            response.addProperty("status", "SUCCESS");
            response.addProperty("message", "Account created successfully. You can now log in.");
        } else {
            response.addProperty("status", "FAILED");
            response.addProperty("message", "Username already exists.");
        }
        return response.toString();
    }

    // XU LY LOGIN
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
        // Goi ham cua Thanh vien 2 de lay danh sach
        response.add("auctions", AuctionDAO.getInstance().getAllAuctionsAsJson());
        return response.toString();
    }
}