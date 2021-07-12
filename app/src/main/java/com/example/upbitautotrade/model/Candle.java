package com.example.upbitautotrade.model;

import com.google.gson.annotations.SerializedName;

public class Candle {
    @SerializedName("market")
    String market;

    @SerializedName("candle_date_time_utc")
    String candleDateTimeUtc;

    @SerializedName("candle_date_time_kst")
    String candleDateTimeKst;

    @SerializedName("opening_price")
    Number openingPrice;

    @SerializedName("high_price")
    Number highPrice;

    @SerializedName("low_price")
    Number lowPrice;

    @SerializedName("trade_price")
    Number tradePrice;

    @SerializedName("timestamp")
    Long timestamp;

    @SerializedName("candle_acc_trade_price")
    Number candleAccTradePrice;

    @SerializedName("candle_acc_trade_volume")
    Number candleAccTradeVolume;

    @SerializedName("unit")
    Integer unit;

    public String getMarketId() {
        return market;
    }

    public String getCandleDateTimeUtc() {
        return candleDateTimeUtc;
    }

    public String getCandleDateTimeKst() {
        return candleDateTimeKst;
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

    public Long getTimestamp() {
        return timestamp;
    }

    public Number getCandleAccTradePrice() {
        return candleAccTradePrice;
    }

    public Number getCandleAccTradeVolume() {
        return candleAccTradeVolume;
    }

    public Integer getUnit() {
        return unit;
    }
}
