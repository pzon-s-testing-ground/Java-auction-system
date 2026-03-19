package com.auction.shared;

import java.time.LocalDateTime;

public class BidTransaction {
    private final String transactionId;
    private final Bidder bidder;
    private final double bidAmount;
    private final LocalDateTime timestamp;

    public BidTransaction(String transactionId, Bidder bidder, double bidAmount) {
        this.transactionId = transactionId;
        this.bidder = bidder;
        this.bidAmount = bidAmount;
        this.timestamp = LocalDateTime.now();
    }

    public String getTransactionId() { return transactionId; }
    public Bidder getBidder() { return bidder; }
    public double getBidAmount() { return bidAmount; }
    public LocalDateTime getTimestamp() { return timestamp; }
}