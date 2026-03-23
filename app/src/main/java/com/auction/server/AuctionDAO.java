package com.auction.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.auction.shared.Auction;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class AuctionDAO {
    private static AuctionDAO instance;
    
    // Dung HashMap lam bo nho dem (Cache) de truy xuat nhanh tren RAM
    private final Map<String, Auction> cache;

    private AuctionDAO() {
        this.cache = new HashMap<>();
    }

    public static synchronized AuctionDAO getInstance() {
        if (instance == null) {
            instance = new AuctionDAO();
        }
        return instance;
    }

    // Luu hoac cap nhat phien dau gia
    public void saveAuction(Auction auction) {
        // 1. Luu vao Cache (RAM)
        cache.put(auction.getAuctionId(), auction);

        // 2. Luu vao Database (O cung)
        // Dung INSERT OR REPLACE de neu id da ton tai, no se cap nhat gia moi
        String sql = "INSERT OR REPLACE INTO auctions (id, item_name, starting_price, current_bid, status) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, auction.getAuctionId());
            pstmt.setString(2, auction.getItem().getName());
            pstmt.setDouble(3, auction.getItem().getStartingPrice());
            pstmt.setDouble(4, auction.getItem().getCurrentHighestBid());
            pstmt.setString(5, auction.getStatus());
            
            pstmt.executeUpdate();
            System.out.println("[Database] Saved/Updated auction " + auction.getAuctionId() + " to SQLite.");
            
        } catch (SQLException e) {
            System.err.println("[Database] Error saving auction: " + e.getMessage());
        }
    }

    // Lay phien dau gia ra (Tam thoi lay tu Cache de toi uu toc do)
    public Auction getAuction(String auctionId) {
        return cache.get(auctionId);
    }

    // Ham nay lay toan bo danh sach dau gia tu SQLite va bien thanh mang JSON
    public JsonArray getAllAuctionsAsJson() {
        JsonArray array = new JsonArray();
        String sql = "SELECT * FROM auctions";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("id", rs.getString("id"));
                obj.addProperty("itemName", rs.getString("item_name"));
                obj.addProperty("currentBid", rs.getDouble("current_bid"));
                obj.addProperty("status", rs.getString("status"));
                array.add(obj);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching auctions: " + e.getMessage());
        }
        return array;
    }
}