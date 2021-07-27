package com.example.upbitautotrade.utils;

import java.io.Serializable;

public class BuyingItem implements Serializable {
    public final String WAITING = "Waiting";
    public final String BUY = "Buy";
    public final String SELL = "Sell";
    String marketId;
    double tradePrice;
    float profitRate;
    double buyingPrice;
    long buyingTime;
    long endTime;
    long startTimeFirst;

    private String status = "Waiting";

    public String getMarketId() {
        return marketId;
    }

    public double getBuyingPrice() {
        return buyingPrice;
    }

    public long getBuyingTime() {
        return buyingTime;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public void setBuyingPrice(double buyingPrice) {
        this.buyingPrice = buyingPrice;
    }

    public void setBuyingTime(long buyingTime) {
        this.buyingTime = buyingTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getStartTimeFirst() {
        return startTimeFirst;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setStartTimeFirst(long startTimeFirst) {
        this.startTimeFirst = startTimeFirst;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
