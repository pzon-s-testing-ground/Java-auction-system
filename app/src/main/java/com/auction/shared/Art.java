package com.auction.shared;

public class Art extends Item {
    private String artistName;
    private int yearCreated;

    public Art(String id, String name, String description, double startingPrice, 
               long startTime, long endTime, String artistName, int yearCreated) {
        super(id, name, description, startingPrice, startTime, endTime);
        this.artistName = artistName;
        this.yearCreated = yearCreated;
    }

    @Override
    public void printInfo() {
        System.out.println("--- Art Item ---");
        System.out.println("Title: " + name + " by " + artistName + " (" + yearCreated + ")");
        System.out.println("Starting Price: $" + startingPrice);
    }

    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }
    public int getYearCreated() { return yearCreated; }
    public void setYearCreated(int yearCreated) { this.yearCreated = yearCreated; }
}