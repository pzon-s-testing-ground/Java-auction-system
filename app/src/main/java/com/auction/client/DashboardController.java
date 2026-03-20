package com.auction.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label statusLabel;
    @FXML private TableView<AuctionInfo> auctionTable;

    private String username;
    // Danh sach dac biet cua JavaFX de tu dong cap nhat TableView khi co thay doi
    private ObservableList<AuctionInfo> auctionList = FXCollections.observableArrayList();

    public void initData(String username) {
        this.username = username;
        welcomeLabel.setText("Logged in as: " + username);
        auctionTable.setItems(auctionList);

        // Chuyen quyen lang nghe mang cho DashboardController
        NetworkManager.getInstance().setCallback(this::processServerResponse);

        // Vua vao man hinh la tu dong tai danh sach luon
        loadAuctions();
    }

    @FXML
    protected void handleRefreshAction(ActionEvent event) {
        loadAuctions();
    }

    private void loadAuctions() {
        JsonObject request = new JsonObject();
        request.addProperty("action", "GET_AUCTIONS");
        NetworkManager.getInstance().sendMessage(request.toString());
        
        statusLabel.setStyle("-fx-text-fill: blue;");
        statusLabel.setText("Loading auctions from server...");
    }

    @FXML
    protected void handleJoinAction(ActionEvent event) {
        // Lay dong du lieu ma nguoi dung dang click chon tren bang
        AuctionInfo selectedAuction = auctionTable.getSelectionModel().getSelectedItem();
        
        if (selectedAuction == null) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Please click on an auction in the table first.");
            return;
        }

        // Gui yeu cau tham gia vao phong do
        JsonObject request = new JsonObject();
        request.addProperty("action", "JOIN_AUCTION");
        request.addProperty("userId", username);
        request.addProperty("auctionId", selectedAuction.getId());
        NetworkManager.getInstance().sendMessage(request.toString());
    }

    private void processServerResponse(String response) {
        try {
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
            
            // Kich ban 1: Server tra ve danh sach phong dau gia
            if (jsonResponse.has("auctions")) {
                JsonArray auctionsArray = jsonResponse.getAsJsonArray("auctions");
                
                auctionList.clear(); // Xoa du lieu cu
                
                for (JsonElement element : auctionsArray) {
                    JsonObject obj = element.getAsJsonObject();
                    String id = obj.get("id").getAsString();
                    String name = obj.get("itemName").getAsString();
                    double currentBid = obj.get("currentBid").getAsDouble();
                    String status = obj.get("status").getAsString();
                    
                    auctionList.add(new AuctionInfo(id, name, currentBid, status));
                }
                statusLabel.setStyle("-fx-text-fill: green;");
                statusLabel.setText("Auctions loaded successfully.");
            }
            // Kich ban 2: Server cho phep tham gia vao phong (Lenh JOIN_AUCTION thanh cong)
            else if (jsonResponse.has("status") && "SUCCESS".equals(jsonResponse.get("status").getAsString()) && jsonResponse.has("itemName")) {
                AuctionInfo selected = auctionTable.getSelectionModel().getSelectedItem();
                
                if (selected != null) {
                    String itemName = jsonResponse.get("itemName").getAsString();
                    double currentBid = jsonResponse.get("currentBid").getAsDouble();

                    // Chuyen canh sang man hinh Bidding Room
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/auction/client/bidding.fxml"));
                    Parent root = loader.load();

                    BiddingController biddingController = loader.getController();
                    biddingController.initData(username, selected.getId(), itemName, currentBid);

                    Stage stage = (Stage) auctionTable.getScene().getWindow();
                    // Mo rong cua so ra mot chut cho phong dau gia
                    stage.setScene(new Scene(root, 450, 350));
                }
            } 
            // Kich ban 3: Loi
            else if (jsonResponse.has("status") && "FAILED".equals(jsonResponse.get("status").getAsString())) {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText(jsonResponse.get("message").getAsString());
            }
        } catch (Exception e) {
            System.err.println("Error processing Dashboard response: " + e.getMessage());
        }
    }
}