package com.auction.client;

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

    @FXML
    private TextField userIdField;
    @FXML
    private TextField auctionIdField;
    @FXML
    private Button joinButton;
    @FXML
    private Label statusLabel;

    @FXML
    protected void handleJoinAction(ActionEvent event) {
        String userId = userIdField.getText().trim();
        String auctionId = auctionIdField.getText().trim();

        if (userId.isEmpty() || auctionId.isEmpty()) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Please enter both User ID and Auction ID.");
            return;
        }

        try {
            NetworkManager net = NetworkManager.getInstance();

            // Chi mo ket noi 1 lan duy nhat khi chua ket noi
            if (!net.isConnected()) {
                net.connect("127.0.0.1", 8080);
            }

            // Dang ky ham lang nghe (Callback) de xu ly chuoi JSON tra ve
            net.setCallback(response -> processServerResponse(response, userId, auctionId));

            // Dong goi va gui Request sang Server
            JsonObject request = new JsonObject();
            request.addProperty("action", "JOIN_AUCTION");
            request.addProperty("userId", userId);
            request.addProperty("auctionId", auctionId);
            net.sendMessage(request.toString());

        } catch (Exception e) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Connection error: " + e.getMessage());
        }
    }

    // Ham nay se duoc goi ngam khi Server gui bat cu tin nhan nao ve
    private void processServerResponse(String response, String userId, String auctionId) {
        try {
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();

            // Kiem tra xem ban tin co truong status khong
            if (jsonResponse.has("status")) {
                String status = jsonResponse.get("status").getAsString();

                if ("SUCCESS".equals(status) && jsonResponse.has("itemName")) {
                    String itemName = jsonResponse.get("itemName").getAsString();
                    double currentBid = jsonResponse.get("currentBid").getAsDouble();

                    // Chuyen canh sang Bidding Room
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/auction/client/bidding.fxml"));
                    Parent root = loader.load();

                    BiddingController biddingController = loader.getController();
                    biddingController.initData(userId, auctionId, itemName, currentBid);

                    Stage stage = (Stage) joinButton.getScene().getWindow();
                    stage.setScene(new Scene(root, 450, 350));
                } else {
                    String message = jsonResponse.has("message") ? jsonResponse.get("message").getAsString() : "Error";
                    statusLabel.setStyle("-fx-text-fill: red;");
                    statusLabel.setText("Failed: " + message);
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing response in Login: " + e.getMessage());
        }
    }
}
