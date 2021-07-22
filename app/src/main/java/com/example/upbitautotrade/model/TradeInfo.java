package com.example.upbitautotrade.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TradeInfo implements Serializable {
    @SerializedName("market")
    String	market;

    @SerializedName("trade_date_utc")
    String	tradeDateUtc;

    @SerializedName("trade_time_utc")
    String	tradeTimeUtc;

    @SerializedName("timestamp")
    long timestamp;

    @SerializedName("trade_price")
    Number	tradePrice;

    @SerializedName("trade_volume")
    Number	tradeVolume;

    @SerializedName("prev_closing_price")
    Number	prevClosingPrice;

    @SerializedName("change_price")
    Number	changePrice;

    @SerializedName("ask_bid")
    String	askBid;

    @SerializedName("sequential_id")
    long sequentialId;

    public String getMarketId() {
        return market;
    }

    public String getTradeDateUtc() {
        return tradeDateUtc;
    }

    public String getTradeTimeUtc() {
        return tradeTimeUtc;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Number getTradePrice() {
        return tradePrice;
    }

    public Number getTradeVolume() {
        return tradeVolume;
    }

    public Number getPrevClosingPrice() {
        return prevClosingPrice;
    }

    public Number getChangePrice() {
        return changePrice;
    }

    public String getAskBid() {
        return askBid;
    }

    public long getSequentialId() {
        return sequentialId;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public void setTradeDateUtc(String tradeDateUtc) {
        this.tradeDateUtc = tradeDateUtc;
    }

    public void setTradeTimeUtc(String tradeTimeUtc) {
        this.tradeTimeUtc = tradeTimeUtc;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setTradePrice(Number tradePrice) {
        this.tradePrice = tradePrice;
    }

    public void setTradeVolume(Number tradeVolume) {
        this.tradeVolume = tradeVolume;
    }

    public void setPrevClosingPrice(Number prevClosingPrice) {
        this.prevClosingPrice = prevClosingPrice;
    }

    public void setChangePrice(Number changePrice) {
        this.changePrice = changePrice;
    }

    public void setAskBid(String askBid) {
        this.askBid = askBid;
    }

    public void setSequentialId(long sequentialId) {
        this.sequentialId = sequentialId;
    }


    //Custom Param.

    long evaluationStartTimeFirst;

    double minPrice;

    long startTime;

    long endTime;

    int tickCount = 0;

    int tickTurn = 0;

    int risingPoint = 0;
    
    public long getEvaluationStartTimeFirst() {
        return evaluationStartTimeFirst;
    }

    public void setEvaluationStartTimeFirst(long evaluationStartTimeFirst) {
        this.evaluationStartTimeFirst = evaluationStartTimeFirst;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public int getTickCount() {
        return tickCount;
    }

    public void setTickCount(int tickCount) {
        this.tickCount = tickCount;
    }

    public int getRisingPoint() {
        return risingPoint;
    }

    public void setRisingPoint(int tickCount) {
        this.risingPoint = tickCount;
    }

    public int getTickTurn() {
        return tickTurn;
    }

    public void setTickTurn(int tickTurn) {
        this.tickTurn = tickTurn;
    }
}
