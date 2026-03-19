package com.auction.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField userIdField;
    @FXML private TextField auctionIdField;
    @FXML private Button joinButton;
    @FXML private Label statusLabel;

    @FXML
    protected void handleJoinAction(ActionEvent event) {
        String userId = userIdField.getText().trim();
        String auctionId = auctionIdField.getText().trim();

        if (userId.isEmpty() || auctionId.isEmpty()) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Please enter both User ID and Auction ID.");
            return;
        }
        sendJoinRequest(userId, auctionId);
    }

    private void sendJoinRequest(String userId, String auctionId) {
        try (Socket socket = new Socket("127.0.0.1", 8080); 
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            JsonObject request = new JsonObject();
            request.addProperty("action", "JOIN_AUCTION");
            request.addProperty("userId", userId);
            request.addProperty("auctionId", auctionId);

            out.println(request.toString());
            String response = in.readLine();
            
            // 1. Parse JSON tra ve tu Server
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
            String status = jsonResponse.get("status").getAsString();

            // 2. Kiem tra status, neu SUCCESS thi chuyen canh
            if ("SUCCESS".equals(status)) {
                // Load file fxml moi
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/auction/client/bidding.fxml"));
                Parent root = loader.load();

                // Lay controller cua man hinh moi de truyen userId va auctionId
                BiddingController biddingController = loader.getController();
                biddingController.initData(userId, auctionId);

                // Lay Stage (cua so hien tai) thong qua nut bam va doi Scene
                Stage stage = (Stage) joinButton.getScene().getWindow();
                stage.setScene(new Scene(root, 450, 350));
            } else {
                // Neu that bai (vi du sai ID), hien thi loi tu Server
                String message = jsonResponse.get("message").getAsString();
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Failed: " + message);
            }

        } catch (Exception e) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Connection error: " + e.getMessage());
        }
    }
}