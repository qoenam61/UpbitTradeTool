package com.example.upbitautotrade.model;

public class OrderInfo {
    private String uuid;
    private String identifier;

    public OrderInfo(String uuid, String identifier) {
        this.uuid = uuid;
        this.identifier = identifier;
    }

    public String getUuid() {
        return uuid;
    }

    public String getIdentifier() {
        return identifier;
    }
}
