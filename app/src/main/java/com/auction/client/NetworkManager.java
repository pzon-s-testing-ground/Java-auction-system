package com.auction.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

import javafx.application.Platform;

public class NetworkManager {
    private static NetworkManager instance;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    
    // Dung de truyen ket qua tu luong lang nghe ngam ve cho Controller
    private Consumer<String> messageCallback;

    private NetworkManager() {}

    public static synchronized NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    public boolean isConnected() {
        return socket != null && !socket.isClosed();
    }

    public void connect(String host, int port) throws Exception {
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Tao luong chay ngam de luon vao vai "Nguoi lang nghe"
        Thread listenerThread = new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    final String finalMsg = message;
                    
                    // Bat buoc phai dung Platform.runLater de cap nhat giao dien JavaFX 
                    // an toan tu mot luong khac
                    Platform.runLater(() -> {
                        if (messageCallback != null) {
                            messageCallback.accept(finalMsg);
                        }
                    });
                }
            } catch (Exception e) {
                System.err.println("Disconnected from server.");
            }
        });
        listenerThread.setDaemon(true); // Tu dong huy luong nay khi tat ung dung
        listenerThread.start();
    }

    public void setCallback(Consumer<String> callback) {
        this.messageCallback = callback;
    }

    public void sendMessage(String jsonMessage) {
        if (out != null) {
            out.println(jsonMessage);
        }
    }
}