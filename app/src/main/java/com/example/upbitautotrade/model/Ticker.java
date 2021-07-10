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
    Double	openingPrice;

    @SerializedName("high_price")
    Double	highPrice;

    @SerializedName("low_price")
    Double	lowPrice;

    @SerializedName("trade_price")
    Double	tradePrice;

    @SerializedName("prev_closing_price")
    Double	prevClosingPrice;

    @SerializedName("change")
    String	change;

    @SerializedName("change_price")
    Double	changePrice;

    @SerializedName("changeRate")
    Double	changeRate;

    @SerializedName("signed_change_price")
    Double	signedChangePrice;

    @SerializedName("signed_change_rate")
    Double	signedChangeRate;

    @SerializedName("trade_volume")
    Double	tradeVolume;

    @SerializedName("acc_trade_price")
    Double	accTradePrice;

    @SerializedName("acc_trade_price_24h")
    Double	accTradePrice24h;

    @SerializedName("acc_trade_volume")
    Double	accTradeVolume;

    @SerializedName("acc_trade_volume_24h")
    Double	accTradeVolume24h;

    @SerializedName("highest_52_week_price")
    Double	highest52WeekPrice;

    @SerializedName("highest_52_week_date")
    String	highest52WeekDate;

    @SerializedName("lowest_52_week_price")
    Double	lowest52WeekPrice;

    @SerializedName("lowest_52_week_date")
    String	lowest52WeekDate;

    @SerializedName("timestamp")
    long timestamp;

    public String getMarket() {
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

    public Double getOpeningPrice() {
        return openingPrice;
    }

    public Double getHighPrice() {
        return highPrice;
    }

    public Double getLowPrice() {
        return lowPrice;
    }

    public Double getTradePrice() {
        return tradePrice;
    }

    public Double getPrevClosingPrice() {
        return prevClosingPrice;
    }

    public String getChange() {
        return change;
    }

    public Double getChangePrice() {
        return changePrice;
    }

    public Number getChangeRate() {
        return changeRate;
    }

    public Double getSignedChangePrice() {
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

    public Double getHighest52WeekPrice() {
        return highest52WeekPrice;
    }

    public String getHighest52WeekDate() {
        return highest52WeekDate;
    }

    public Double getLowest52WeekPrice() {
        return lowest52WeekPrice;
    }

    public String getLowest52WeekDate() {
        return lowest52WeekDate;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
