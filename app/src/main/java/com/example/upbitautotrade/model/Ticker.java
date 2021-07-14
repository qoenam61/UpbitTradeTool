package com.example.upbitautotrade.model;

import com.google.gson.annotations.SerializedName;

public class Ticker {

    @SerializedName("market")
    String	market;

    @SerializedName("trade_date")
    String	tradeDate;

    @SerializedName("trade_time")
    String	tradeTime;

    @SerializedName("trade_date_kst")
    String	tradeDateKst;

    @SerializedName("trade_time_kst")
    String	tradeTimeKst;

    @SerializedName("trade_timestamp")
    long	tradeTimestamp;

    @SerializedName("opening_price")
    Number	openingPrice;

    @SerializedName("high_price")
    Number	highPrice;

    @SerializedName("low_price")
    Number	lowPrice;

    @SerializedName("trade_price")
    Number	tradePrice;

    @SerializedName("prev_closing_price")
    Number	prevClosingPrice;

    @SerializedName("change")
    String	change;

    @SerializedName("change_price")
    Number	changePrice;

    @SerializedName("changeRate")
    Number	changeRate;

    @SerializedName("signed_change_price")
    Number	signedChangePrice;

    @SerializedName("signed_change_rate")
    Number	signedChangeRate;

    @SerializedName("trade_volume")
    Number	tradeVolume;

    @SerializedName("acc_trade_price")
    Number	accTradePrice;

    @SerializedName("acc_trade_price_24h")
    Number	accTradePrice24h;

    @SerializedName("acc_trade_volume")
    Number	accTradeVolume;

    @SerializedName("acc_trade_volume_24h")
    Number	accTradeVolume24h;

    @SerializedName("highest_52_week_price")
    Number	highest52WeekPrice;

    @SerializedName("highest_52_week_date")
    String	highest52WeekDate;

    @SerializedName("lowest_52_week_price")
    Number	lowest52WeekPrice;

    @SerializedName("lowest_52_week_date")
    String	lowest52WeekDate;

    @SerializedName("timestamp")
    long timestamp;

    public String getMarketId() {
        return market;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public String getTradeTime() {
        return tradeTime;
    }

    public String getTradeDateKst() {
        return tradeDateKst;
    }

    public String getTradeTimeKst() {
        return tradeTimeKst;
    }

    public long getTradeTimestamp() {
        return tradeTimestamp;
    }

    public Number getOpeningPrice() {
        return openingPrice;
    }

    public Number getHighPrice() {
        return highPrice;
    }

    public Number getLowPrice() {
        return lowPrice;
    }

    public Number getTradePrice() {
        return tradePrice;
    }

    public Number getPrevClosingPrice() {
        return prevClosingPrice;
    }

    public String getChange() {
        return change;
    }

    public Number getChangePrice() {
        return changePrice;
    }

    public Number getChangeRate() {
        return changeRate;
    }

    public Number getSignedChangePrice() {
        return signedChangePrice;
    }

    public Number getSignedChangeRate() {
        return signedChangeRate;
    }

    public Number getTradeVolume() {
        return tradeVolume;
    }

    public Number getAccTradePrice() {
        return accTradePrice;
    }

    public Number getAccTradePrice24h() {
        return accTradePrice24h;
    }

    public Number getAccTradeVolume() {
        return accTradeVolume;
    }

    public Number getAccTradeVolume24h() {
        return accTradeVolume24h;
    }

    public Number getHighest52WeekPrice() {
        return highest52WeekPrice;
    }

    public String getHighest52WeekDate() {
        return highest52WeekDate;
    }

    public Number getLowest52WeekPrice() {
        return lowest52WeekPrice;
    }

    public String getLowest52WeekDate() {
        return lowest52WeekDate;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
