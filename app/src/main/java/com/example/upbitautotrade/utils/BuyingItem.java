package com.example.upbitautotrade.utils;

import java.io.Serializable;

public class BuyingItem implements Serializable {
    String marketId;
    double tradePrice;
    float profitRate;
    double buyingPrice;
    long buyingTime;

    long endTime;

    long startTimeFirst;

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
}
