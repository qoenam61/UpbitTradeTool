package com.example.upbitautotrade.utils;

public class Item {
    String key;
    int type;
    int unit;
    String to = null;
    int count = 0;
    String priceUnit;
    String cursor = null;
    int daysAgo = -1;

    public Item(String key, int type) {
        this.key = key;
        this.type = type;
    }

    public Item(String key, int type, String to, int count, String cursor, int daysAgo) {
        this.key = key;
        this.type = type;
        this.to = to;
        this.count = count;
        this.cursor = cursor;
        this.daysAgo = daysAgo;
    }

    public Item(int unit, String key, int type, String to, int count) {
        this.key = key;
        this.type = type;
        this.unit = unit;
        this.to = to;
        this.count = count;
    }

    public Item(String key, int type, String to, int count) {
        this.key = key;
        this.type = type;
        this.to = to;
        this.count = count;
    }

    public Item(String key, int type, String to, int count, String priceUnit) {
        this.key = key;
        this.type = type;
        this.to = to;
        this.count = count;
        this.priceUnit = priceUnit;
    }
}