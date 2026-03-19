package com.auction.shared;

public class Seller extends User {
    private double sellerRating;

    public Seller(String id, String username, String password, String email) {
        super(id, username, password, email);
        this.sellerRating = 0.0; // Mac dinh khi moi tao
    }

    @Override
    public void displayRole() {
        System.out.println("Role: Seller - " + this.username + " (Can post items for auction)");
    }

    public double getSellerRating() {
        return sellerRating;
    }

    public void setSellerRating(double sellerRating) {
        this.sellerRating = sellerRating;
    }
}