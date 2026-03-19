package com.auction.shared;

import java.util.ArrayList;
import java.util.List;

public class Auction {
    private String auctionId;
    private Item item;
    private Seller seller;
    private List<BidTransaction> bidHistory;
    private String status; // OPEN, RUNNING, FINISHED, PAID, CANCELED
    private Bidder currentWinner;

    public Auction(String auctionId, Item item, Seller seller) {
        this.auctionId = auctionId;
        this.item = item;
        this.seller = seller;
        this.bidHistory = new ArrayList<>();
        this.status = "OPEN";
    }

    // Su dung synchronized de dam bao an toan luong (Thread-safe) khi nhieu nguoi dat gia cung luc
    public synchronized boolean placeBid(BidTransaction bid) {
        if (!status.equals("RUNNING")) {
            return false; // Phien dau gia chua mo hoac da ket thuc
        }

        if (bid.getBidAmount() > item.getCurrentHighestBid()) {
            item.setCurrentHighestBid(bid.getBidAmount());
            this.currentWinner = bid.getBidder();
            this.bidHistory.add(bid);
            return true;
        }
        
        return false; // Gia dat khong hop le (thap hon hoac bang gia hien tai)
    }

    public void startAuction() {
        this.status = "RUNNING";
    }

    public void endAuction() {
        this.status = "FINISHED";
    }

    // Getters and Setters
    public String getAuctionId() { return auctionId; }
    public Item getItem() { return item; }
    public Seller getSeller() { return seller; }
    public List<BidTransaction> getBidHistory() { return bidHistory; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Bidder getCurrentWinner() { return currentWinner; }
}