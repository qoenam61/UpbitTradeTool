package com.example.upbitautotrade.utils;

import java.io.Serializable;

public class BuyingItem implements Serializable {
    String marketId;
    int tradePrice;
    float profitRate;
    int buyingPrice;
    int buyingAmount;

    public String getMarketId() {
        return marketId;
    }

    public int getBuyingPrice() {
        return buyingPrice;
    }

    public int getBuyingAmount() {
        return buyingAmount;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public void setBuyingPrice(int buyingPrice) {
        this.buyingPrice = buyingPrice;
    }

    public void setBuyingAmount(int buyingAmount) {
        this.buyingAmount = buyingAmount;
    }
}
