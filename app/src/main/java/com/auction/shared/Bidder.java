package com.auction.shared;

public class Bidder extends User {
    public Bidder(String id, String username, String password, String email) {
        super(id, username, password, email);
    }

    @Override
    public void displayRole() {
        System.out.println("Role: Bidder - " + this.username + " (Can participate in auctions)");
    }
}