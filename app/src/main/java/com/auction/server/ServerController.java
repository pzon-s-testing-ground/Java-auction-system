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
                case "JOIN_AUCTION" -> handleJoinAuction(request);
                case "PLACE_BID" -> handlePlaceBid(request);
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
        JsonObject response = new JsonObject();
        response.addProperty("status", "SUCCESS");
        response.addProperty("message", "User " + userId + " successfully joined auction " + auctionId);
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
            response.addProperty("status", "SUCCESS");
            response.addProperty("message", "Bid placed successfully. Current highest bid is now $" + auction.getItem().getCurrentHighestBid());
        } else {
            response.addProperty("status", "FAILED");
            response.addProperty("message", "Bid rejected. Amount must be higher than current bid ($" + auction.getItem().getCurrentHighestBid() + ") and auction must be RUNNING.");
        }

        return response.toString();
    }
}