package com.auction.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class BiddingController {

    @FXML private Label itemLabel;
    @FXML private Label priceLabel;
    @FXML private TextField bidAmountField;
    @FXML private Label messageLabel;

    private String userId;
    private String auctionId;

    public void initData(String userId, String auctionId, String itemName, double currentBid) {
        this.userId = userId;
        this.auctionId = auctionId;
        
        itemLabel.setText("Item: " + itemName);
        priceLabel.setText("Current Highest Bid: $" + currentBid);
        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText("Welcome " + userId + "! You are in auction " + auctionId);

        // Lang nghe tin nhan tu Server thong qua mang luoi chung
        NetworkManager.getInstance().setCallback(this::processServerResponse);
    }

    @FXML
    protected void handlePlaceBid(ActionEvent event) {
        String bidText = bidAmountField.getText().trim();
        if (bidText.isEmpty()) return;

        try {
            double bidAmount = Double.parseDouble(bidText);
            
            // TUYET DOI KHONG DUNG "new Socket()" O DAY NUA
            // Dung NetworkManager de gui qua duong truyen dang mo san
            JsonObject request = new JsonObject();
            request.addProperty("action", "PLACE_BID");
            request.addProperty("userId", userId);
            request.addProperty("auctionId", auctionId);
            request.addProperty("bidAmount", bidAmount);

            NetworkManager.getInstance().sendMessage(request.toString());
            bidAmountField.clear();

        } catch (NumberFormatException e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Invalid amount.");
        }
    }

    private void processServerResponse(String response) {
        try {
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            
            if (json.has("action") && "BROADCAST_NEW_BID".equals(json.get("action").getAsString())) {
                double newBid = json.get("newBid").getAsDouble();
                String bidder = json.get("bidder").getAsString();
                
                // Giao dien tu dong nhay so
                priceLabel.setText("Current Highest Bid: $" + newBid);
                
                if (this.userId.equals(bidder)) {
                    messageLabel.setStyle("-fx-text-fill: green;");
                    messageLabel.setText("Your bid was successful!");
                } else {
                    messageLabel.setStyle("-fx-text-fill: #b8860b;");
                    messageLabel.setText("User " + bidder + " just placed a new bid!");
                }
            } 
            else if (json.has("status") && "FAILED".equals(json.get("status").getAsString())) {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText(json.get("message").getAsString());
            }
        } catch (Exception e) {
            System.err.println("Error parsing bidding response: " + e.getMessage());
        }
    }
}