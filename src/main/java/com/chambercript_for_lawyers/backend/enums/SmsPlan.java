package com.chambercript_for_lawyers.backend.enums;

public enum SmsPlan {
    NONE(500, 0.0),
    BASIC(1000, 2500.0),
    PRO(2000, 4500.0),
    UNLIMITED(-1, 7000.0); // -1 signifies unlimited

    private final int quota;
    private final double price;

    SmsPlan(int quota, double price) {
        this.quota = quota;
        this.price = price;
    }

    public int getQuota() {
        return quota;
    }

    public double getPrice() {
        return price;
    }
}