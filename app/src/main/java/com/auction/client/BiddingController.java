package com.auction.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
        
        // Hien thi du lieu thuc te lay tu Database
        itemLabel.setText("Item: " + itemName);
        priceLabel.setText("Current Highest Bid: $" + currentBid);
        
        messageLabel.setStyle("-fx-text-fill: green;");
        messageLabel.setText("Welcome " + userId + "! You are in auction " + auctionId);
    }

    @FXML
    protected void handlePlaceBid(ActionEvent event) {
        String bidText = bidAmountField.getText().trim();
        
        if (bidText.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Please enter a bid amount.");
            return;
        }

        try {
            // Chuyen chuoi nhap vao thanh so thuc (double)
            double bidAmount = Double.parseDouble(bidText);
            sendBidToServer(bidAmount);
        } catch (NumberFormatException e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Invalid amount. Please enter numbers only.");
        }
    }

    private void sendBidToServer(double bidAmount) {
        try (Socket socket = new Socket("127.0.0.1", 8080);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Tao request JSON de dat gia
            JsonObject request = new JsonObject();
            request.addProperty("action", "PLACE_BID");
            request.addProperty("userId", userId);
            request.addProperty("auctionId", auctionId);
            request.addProperty("bidAmount", bidAmount);

            // Gui len Server
            out.println(request.toString());
            
            // Nhan phan hoi
            String response = in.readLine();
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
            String status = jsonResponse.get("status").getAsString();
            String message = jsonResponse.get("message").getAsString();

            if ("SUCCESS".equals(status)) {
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Success! " + message);
                
                // Cap nhat lai gia hien thi tren giao dien
                priceLabel.setText("Current Highest Bid: $" + bidAmount);
                bidAmountField.clear(); // Xoa trang o nhap lieu
            } else {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("Failed: " + message);
            }

        } catch (Exception e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Connection error: " + e.getMessage());
        }
    }
}