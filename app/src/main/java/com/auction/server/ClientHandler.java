package com.auction.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ServerController controller;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.controller = new ServerController();
    }

    @Override
    public void run() {
        // Tu khoa 'true' trong PrintWriter giup tu dong day du lieu (auto-flush) ngay lap tuc
        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            String inputLine;
            
            // VONG LAP NAY CUC KY QUAN TRONG: Giu cho Socket luon mo de lang nghe Client 24/7
            while ((inputLine = in.readLine()) != null) {
                System.out.println("[Server] Received from client: " + inputLine);
                
                // Dua request cho Controller xu ly, kem theo 'out' de Controller biet duong ghi danh vao SessionManager
                String response = controller.processRequest(inputLine, out);
                
                // Tra loi rieng cho Client vua gui yeu cau
                if (response != null && !response.isEmpty()) {
                    out.println(response);
                }
            }
        } catch (Exception e) {
            System.out.println("[Server] Client disconnected: " + clientSocket.getInetAddress().getHostAddress());
        }
    }
}