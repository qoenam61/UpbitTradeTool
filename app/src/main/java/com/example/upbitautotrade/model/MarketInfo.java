package com.example.upbitautotrade.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MarketInfo implements Serializable {
    @SerializedName("market")
    private String market;

    @SerializedName("korean_name")
    private String koreanName;

    @SerializedName("english_name")
    private String englishName;

    @SerializedName("market_warning")
    private String marketWarning;

    public String getMarket() {
        return market;
    }

    public String getKorean_name() {
        return koreanName;
    }

    public String getEnglish_name() {
        return englishName;
    }

    public String getMarket_warning() {
        return marketWarning;
    }
}
