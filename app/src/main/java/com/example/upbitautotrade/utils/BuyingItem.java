package com.example.upbitautotrade.utils;

import java.io.Serializable;

public class BuyingItem implements Serializable {
    String marketId;
    int tradePrice;
    float profitRate;
    int buyingPrice;
    long buyingTime;

    public String getMarketId() {
        return marketId;
    }

    public int getBuyingPrice() {
        return buyingPrice;
    }

    public long getBuyingTime() {
        return buyingTime;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public void setBuyingPrice(int buyingPrice) {
        this.buyingPrice = buyingPrice;
    }

    public void setBuyingTime(long buyingTime) {
        this.buyingTime = buyingTime;
    }
}
