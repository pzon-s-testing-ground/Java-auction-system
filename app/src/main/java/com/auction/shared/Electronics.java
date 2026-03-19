package com.auction.shared;

public class Electronics extends Item {
    private String brand;
    private int warrantyMonths;

    public Electronics(String id, String name, String description, double startingPrice, 
                       long startTime, long endTime, String brand, int warrantyMonths) {
        super(id, name, description, startingPrice, startTime, endTime);
        this.brand = brand;
        this.warrantyMonths = warrantyMonths;
    }

    @Override
    public void printInfo() {
        System.out.println("--- Electronics Item ---");
        System.out.println("Name: " + name + " (Brand: " + brand + ")");
        System.out.println("Starting Price: $" + startingPrice);
        System.out.println("Warranty: " + warrantyMonths + " months");
    }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public int getWarrantyMonths() { return warrantyMonths; }
    public void setWarrantyMonths(int warrantyMonths) { this.warrantyMonths = warrantyMonths; }
}