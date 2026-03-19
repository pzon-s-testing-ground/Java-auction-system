package com.auction.shared;

public class Admin extends User {
    public Admin(String id, String username, String password, String email) {
        super(id, username, password, email);
    }

    @Override
    public void displayRole() {
        System.out.println("Role: Admin - " + this.username + " (System Administrator)");
    }
}