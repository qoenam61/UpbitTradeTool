package com.example.upbitautotrade.model;

import com.google.gson.annotations.SerializedName;

public class TradeInfo {
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
}
