package com.auction.server;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    // Luu tru danh sach cac luong gui du lieu (PrintWriter) theo tung auctionId
    private static final Map<String, List<PrintWriter>> rooms = new ConcurrentHashMap<>();

    // Them mot client vao phong
    public static synchronized void joinRoom(String auctionId, PrintWriter out) {
        rooms.computeIfAbsent(auctionId, k -> new ArrayList<>()).add(out);
        System.out.println("[Session] A client joined room " + auctionId + ". Total clients: " + rooms.get(auctionId).size());
    }

    // Phat song tin nhan den TAT CA client trong cung mot phong
    public static synchronized void broadcast(String auctionId, String message) {
        List<PrintWriter> clients = rooms.getOrDefault(auctionId, new ArrayList<>());
        for (PrintWriter out : clients) {
            out.println(message);
        }
    }
}