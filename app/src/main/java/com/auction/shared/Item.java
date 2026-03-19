package com.auction.shared;

public abstract class Item extends Entity {
    protected String name;
    protected String description;
    protected double startingPrice;
    protected double currentHighestBid;
    protected long startTime;
    protected long endTime;

    public Item(String id, String name, String description, double startingPrice, long startTime, long endTime) {
        super(id);
        this.name = name;
        this.description = description;
        this.startingPrice = startingPrice;
        this.currentHighestBid = startingPrice; // Mac dinh gia hien tai bang gia khoi diem
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Abstract method cho cac loai Item cu the
    public abstract void printInfo();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getStartingPrice() {
        return startingPrice;
    }

    public double getCurrentHighestBid() {
        return currentHighestBid;
    }

    public void setCurrentHighestBid(double currentHighestBid) {
        this.currentHighestBid = currentHighestBid;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    
    // Cac getter/setter khac co the bo sung them
}