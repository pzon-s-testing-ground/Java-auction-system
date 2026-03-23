package com.auction.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        // Doc file giao dien login.fxml tu thu muc resources
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/auction/client/auth.fxml"));
        
        // Tao Scene (khung hinh) voi kich thuoc 400x300
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);
        
        stage.setTitle("Online Auction System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}