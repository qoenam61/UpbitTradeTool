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

    public double getChangedVolumeRate() {
        double prevRate =  (getTradePrice().doubleValue() - getLowPrice().doubleValue()) / getLowPrice().doubleValue();
        return (double)Math.round(prevRate * 1000) / 1000;
    }

    public double getCenterChangedRate() {
        double centerPrice = (Math.pow(getHighPrice().doubleValue(), 2)
                + Math.pow(getTradePrice().doubleValue(), 2)
                + Math.pow(getOpeningPrice().doubleValue(), 2)
                + Math.pow(getLowPrice().doubleValue(), 2)) / 4;
        centerPrice = CoinInfo.convertPrice(Math.sqrt(centerPrice));
        double prevRate =  (getTradePrice().doubleValue() - centerPrice) / centerPrice;
        return (double)Math.round(prevRate * 1000) / 1000;
    }

    @Override
    public int compareTo(Candle o) {
        double originalData = this.getCandleAccTradePrice().doubleValue() * this.getChangedVolumeRate();
        double compareData = o.getCandleAccTradePrice().doubleValue() * ((DayCandle)o).getChangedVolumeRate();

        if (originalData < compareData) {
            return 1;
        } else if (originalData > compareData) {
            return -1;
        } else {
            return 0;
        }
    }
}
