package com.auction.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AuthController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    @FXML
    protected void handleSignupAction(ActionEvent event) {
        sendAuthRequest("SIGNUP");
    }

    @FXML
    protected void handleLoginAction(ActionEvent event) {
        sendAuthRequest("LOGIN");
    }

    private void sendAuthRequest(String actionType) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Username and password cannot be empty.");
            return;
        }

        try {
            NetworkManager net = NetworkManager.getInstance();
            if (!net.isConnected()) {
                net.connect("127.0.0.1", 8080);
            }

            net.setCallback(this::processAuthResponse);

            JsonObject request = new JsonObject();
            request.addProperty("action", actionType);
            request.addProperty("username", username);
            request.addProperty("password", password);
            net.sendMessage(request.toString());

        } catch (Exception e) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Connection error: " + e.getMessage());
        }
    }

    private void processAuthResponse(String response) {
        try {
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
            if (jsonResponse.has("status")) {
                String status = jsonResponse.get("status").getAsString();
                String message = jsonResponse.get("message").getAsString();

                if ("SUCCESS".equals(status)) {
                    statusLabel.setStyle("-fx-text-fill: green;");
                    statusLabel.setText(message);
                    
                    // Neu la LOGIN thanh cong, tam thoi in ra de biet, buoc tiep theo ta se chuyen sang Dashboard
                    if (jsonResponse.has("username")) {
                        String loggedInUser = jsonResponse.get("username").getAsString();
                        
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/auction/client/dashboard.fxml"));
                        Parent root = loader.load();

                        DashboardController dashboardController = loader.getController();
                        dashboardController.initData(loggedInUser);

                        Stage stage = (Stage) statusLabel.getScene().getWindow();
                        // Dashboard can khong gian rong hon de hien thi bang
                        stage.setScene(new Scene(root, 550, 450));
                    }
                } else {
                    statusLabel.setStyle("-fx-text-fill: red;");
                    statusLabel.setText(message);
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing auth response: " + e.getMessage());
        }
    }
}