package com.example.upbitautotrade.model;

public class Monitor {
    String marketId;
    int point;
    int firstPointPrice;

    public String getMarketId() {
        return marketId;
    }

    public int getPoint() {
        return point;
    }

    public int getFirstPointPrice() {
        return firstPointPrice;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public void setFirstPointPrice(int firstPointPrice) {
        this.firstPointPrice = firstPointPrice;
    }
}
