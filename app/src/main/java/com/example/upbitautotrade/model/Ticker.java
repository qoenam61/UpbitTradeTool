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
    Integer	tradeTimestamp;

    @SerializedName("opening_price")
    Integer	openingPrice;

    @SerializedName("high_price")
    Integer	highPrice;

    @SerializedName("low_price")
    Integer	lowPrice;

    @SerializedName("trade_price")
    Integer	tradePrice;

    @SerializedName("prev_closing_price")
    Integer	prevClosingPrice;

    @SerializedName("change")
    String	change;

    @SerializedName("change_price")
    Integer	changePrice;

    @SerializedName("changeRate")
    Number	changeRate;

    @SerializedName("signed_change_price")
    Integer	signedChangePrice;

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
    Integer	highest52WeekPrice;

    @SerializedName("highest_52_week_date")
    String	highest52WeekDate;

    @SerializedName("lowest_52_week_price")
    Integer	lowest52WeekPrice;

    @SerializedName("lowest_52_week_date")
    String	lowest52WeekDate;

    @SerializedName("timestamp")
    Integer	timestamp;

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

    public Integer getTradeTimestamp() {
        return tradeTimestamp;
    }

    public Integer getOpeningPrice() {
        return openingPrice;
    }

    public Integer getHighPrice() {
        return highPrice;
    }

    public Integer getLowPrice() {
        return lowPrice;
    }

    public Integer getTradePrice() {
        return tradePrice;
    }

    public Integer getPrevClosingPrice() {
        return prevClosingPrice;
    }

    public String getChange() {
        return change;
    }

    public Integer getChangePrice() {
        return changePrice;
    }

    public Number getChangeRate() {
        return changeRate;
    }

    public Integer getSignedChangePrice() {
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

    public Integer getHighest52WeekPrice() {
        return highest52WeekPrice;
    }

    public String getHighest52WeekDate() {
        return highest52WeekDate;
    }

    public Integer getLowest52WeekPrice() {
        return lowest52WeekPrice;
    }

    public String getLowest52WeekDate() {
        return lowest52WeekDate;
    }

    public Integer getTimestamp() {
        return timestamp;
    }
}
