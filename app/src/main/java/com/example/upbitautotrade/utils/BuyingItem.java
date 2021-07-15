package com.example.upbitautotrade.utils;

public class BuyingItem {
    String marketId;
    int tradePrice;
    float profitRate;
    int buyingPrice;
    int buyingAmount;

    public String getMarketId() {
        return marketId;
    }

    public int getTradePrice() {
        return tradePrice;
    }

    public float getProfitRate() {
        return profitRate;
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

    public void setTradePrice(int tradePrice) {
        this.tradePrice = tradePrice;
    }

    public void setProfitRate(float profitRate) {
        this.profitRate = profitRate;
    }

    public void setBuyingPrice(int buyingPrice) {
        this.buyingPrice = buyingPrice;
    }

    public void setBuyingAmount(int buyingAmount) {
        this.buyingAmount = buyingAmount;
    }
}
