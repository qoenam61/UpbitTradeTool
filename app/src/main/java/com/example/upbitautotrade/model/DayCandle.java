package com.example.upbitautotrade.model;

import com.google.gson.annotations.SerializedName;

public class DayCandle extends Candle{

    @SerializedName("prev_closing_price")
    Number prevClosingPrice;

    @SerializedName("change_price")
    Number changePrice;

    @SerializedName("change_rate")
    Number changeRate;

    @SerializedName("converted_trade_price")
    Number convertedTrade_price;

    public Number getPrevClosingPrice() {
        return prevClosingPrice;
    }

    public Number getChangePrice() {
        return changePrice;
    }

    public Number getChangeRate() {
        return changeRate;
    }

    public Number getConvertedTrade_price() {
        return convertedTrade_price;
    }
}
