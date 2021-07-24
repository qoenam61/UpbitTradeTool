package com.example.upbitautotrade.utils;

public class Item {
    int type;
    String key;
    int count = 0;
    int unit;
    int daysAgo = -1;
    String to = null;
    String cursor = null;
    String priceUnit = null;

    public Item(int type, String key) {
        this.type = type;
        this.key = key;
    }

    public Item(int type, String key, int count, int unit, int daysAgo, String to, String cursor, String priceUnit) {
        this.type = type;
        this.key = key;
        this.count = count;
        this.unit = unit;
        this.daysAgo = daysAgo;
        this.to = to;
        this.cursor = cursor;
        this.priceUnit = priceUnit;
    }
}