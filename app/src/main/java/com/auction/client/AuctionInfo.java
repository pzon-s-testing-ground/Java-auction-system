package com.auction.client;

public class AuctionInfo {
    private String id;
    private String name;
    private double currentBid;
    private String status;

    public AuctionInfo(String id, String name, double currentBid, String status) {
        this.id = id;
        this.name = name;
        this.currentBid = currentBid;
        this.status = status;
    }

    // JavaFX TableView bat buoc phai co cac ham Getter nay de doc du lieu
    public String getId() { return id; }
    public String getName() { return name; }
    public double getCurrentBid() { return currentBid; }
    public String getStatus() { return status; }
}