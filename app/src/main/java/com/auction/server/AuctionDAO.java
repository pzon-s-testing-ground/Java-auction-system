package com.auction.server;

import java.util.HashMap;
import java.util.Map;

import com.auction.shared.Auction;

public class AuctionDAO {
    // Ap dung Singleton Pattern 
    private static AuctionDAO instance;
    private final Map<String, Auction> database;

    private AuctionDAO() {
        this.database = new HashMap<>();
    }

    public static synchronized AuctionDAO getInstance() {
        if (instance == null) {
            instance = new AuctionDAO();
        }
        return instance;
    }

    // Luu mot phien dau gia vao "database"
    public void saveAuction(Auction auction) {
        database.put(auction.getAuctionId(), auction);
    }

    // Lay phien dau gia ra
    public Auction getAuction(String auctionId) {
        return database.get(auctionId);
    }
}