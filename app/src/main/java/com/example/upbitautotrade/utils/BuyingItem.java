package com.example.upbitautotrade.utils;

import java.io.Serializable;

public class BuyingItem implements Serializable {
    String marketId;
    double tradePrice;
    float profitRate;
    double buyingPrice;
    long buyingTime;

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
}
