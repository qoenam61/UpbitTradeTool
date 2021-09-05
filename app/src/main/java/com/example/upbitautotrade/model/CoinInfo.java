package com.example.upbitautotrade.model;


import java.text.DecimalFormat;
import java.util.UUID;

public class CoinInfo {
    String marketId;
    double openPrice;
    double closePrice;
    double highPrice;
    double lowPrice;

    long waitTime = -1;
    long buyTime;
    long sellTime;

    double buyPrice;
    double sellPrice;

    double maxProfitRate = 0;
    double buyingAmount;

    double tickCounts;

    private String status = "Waiting";

    private boolean isPartialBuy = false;

    private double mVolume = -1;

    private String uuid;

    public static final String READY = "Ready";
    public static final String WAITING = "Waiting";
    public static final String BUY = "Buy";
    public static final String SELL = "Sell";

    public CoinInfo(double openPrice, double closePrice, double highPrice, double lowPrice, double tickCounts) {
        this.openPrice = openPrice;
        this.closePrice = closePrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.tickCounts = tickCounts;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public String getStatus() {
        return status;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public void setBuyTime(long buyTime) {
        this.buyTime = buyTime;
    }

    public long getBuyTime() {
        return buyTime;
    }

    public void setSellTime(long sellTime) {
        this.sellTime = sellTime;
    }

    public long getSellTime() {
        return sellTime;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public void setVolume(double mVolume) {
        this.mVolume = mVolume;
    }

    public double getVolume() {
        return mVolume;
    }

    public void setMaxProfitRate(double currentPrice) {
        double changedPrice = currentPrice - getBuyPrice();
        double changedRate = changedPrice / getBuyPrice();
        changedRate = (double)Math.round(changedRate * 1000) / 1000;

        if (maxProfitRate == 0) {
            maxProfitRate = changedRate;
        } else {
            maxProfitRate = Math.max(maxProfitRate, changedRate);
        }
    }

    public double getMaxProfitRate() {
        return maxProfitRate;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public String getMarketId() {
        return marketId;
    }

    public double getBuyAmount() {
        return getBuyPrice() * mVolume;
    }

    public double getSellAmount() {
        return getSellPrice() * mVolume;
    }

    public double getProfitAmount() {
        return getSellAmount() - getBuyAmount();
    }

    public void setPartialBuy(boolean partialBuy) {
        isPartialBuy = partialBuy;
    }

    public boolean isPartialBuy() {
        return isPartialBuy;
    }

    public double getOpenPrice() {
        return convertPrice(openPrice);
    }

    public void setOpenPrice(double openPrice) {
        this.openPrice = openPrice;
    }

    public double getClosePrice() {
        return convertPrice(closePrice);
    }

    public void setClosePrice(double closePrice) {
        this.closePrice = closePrice;
    }

    public double getHighPrice() {
        return convertPrice(highPrice);
    }

    public void setHighPrice(double highPrice) {
        this.highPrice = highPrice;
    }

    public void updateHighLowClosePrice(double currentPrice) {
        this.highPrice = Math.max(highPrice, currentPrice);
        this.lowPrice = Math.min(lowPrice, currentPrice);
        this.closePrice = currentPrice;
    }

    public double getLowPrice() {
        return convertPrice(lowPrice);
    }

    public void setLowPrice(double lowPrice) {
        this.lowPrice = lowPrice;
    }

    public double getTickCounts() {
        return tickCounts;
    }

    public void setTickCounts(double tickCounts) {
        this.tickCounts = tickCounts;
    }

    public static double convertPrice(double price) {
        DecimalFormat mFormatUnder10 = new DecimalFormat("#.##");
        DecimalFormat mFormatUnder100 = new DecimalFormat("##.#");
        DecimalFormat mFormatUnder1_000 = new DecimalFormat("###");
        DecimalFormat mFormatUnder10_000 = new DecimalFormat("####");
        DecimalFormat mFormatUnder100_000 = new DecimalFormat("#####");
        DecimalFormat mFormatUnder1_000_000 = new DecimalFormat("######");
        DecimalFormat mFormatUnder10_000_000 = new DecimalFormat("#######");
        DecimalFormat mFormatUnder100_000_000 = new DecimalFormat("########");

        String result = null;
        double priceResult = 0;
        if (price < 10) {
            priceResult = Math.floor(price * 100) / 100;
            result = mFormatUnder10.format(priceResult);
        } else if (price < 100) {
            priceResult = Math.floor(price * 10) / 10;
            result = mFormatUnder100.format(priceResult);
        } else if (price < 1000) {
            priceResult = Math.floor(price);
            result = mFormatUnder1_000.format(priceResult);
        } else if (price < 10000) {
            // 5
             double extra = Math.round(((price % 10) * 2) / 10 ) * 5;
            priceResult = Math.floor(price / 10) * 10 + extra;
            result = mFormatUnder10_000.format(priceResult);
        } else if (price < 100000) {
            // 10
            double extra = Math.round(((price % 100)) / 100 ) * 100;
            priceResult = Math.floor(price / 100) * 100 + extra;
            result = mFormatUnder100_000.format(priceResult);
        } else if (price < 1000000) {
            // 50, 100
            double extra = 0;
            if (price < 500000) {
                extra = Math.round(((price % 100) * 2) / 100) * 50;
            } else {
                extra = Math.round(((price % 100)) / 100) * 100;
            }
            priceResult = Math.floor(price / 100) * 100 + extra;
            result = mFormatUnder1_000_000.format(priceResult);
        } else if (price < 10000000) {
            // 1000
            double extra = Math.round(((price % 1000)) / 1000) * 1000;
            priceResult = Math.floor(price / 1000) * 1000 + extra;
            result = mFormatUnder10_000_000.format(priceResult);
        } else if (price < 100000000) {
            // 1000
            double extra = Math.round(((price % 1000)) / 1000) * 1000;
            priceResult = Math.floor(price / 1000) * 1000 + extra;
            result = mFormatUnder100_000_000.format(priceResult);
        }
        return result != null ? Double.parseDouble(result) : null;
    }
}
