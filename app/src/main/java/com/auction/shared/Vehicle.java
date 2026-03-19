package com.auction.shared;

public class Vehicle extends Item {
    private String make;
    private String model;
    private int year;
    private double mileage;

    public Vehicle(String id, String name, String description, double startingPrice, 
                   long startTime, long endTime, String make, String model, int year, double mileage) {
        super(id, name, description, startingPrice, startTime, endTime);
        this.make = make;
        this.model = model;
        this.year = year;
        this.mileage = mileage;
    }

    @Override
    public void printInfo() {
        System.out.println("--- Vehicle Item ---");
        System.out.println("Vehicle: " + year + " " + make + " " + model);
        System.out.println("Mileage: " + mileage + " km");
        System.out.println("Starting Price: $" + startingPrice);
    }

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public double getMileage() { return mileage; }
    public void setMileage(double mileage) { this.mileage = mileage; }
}