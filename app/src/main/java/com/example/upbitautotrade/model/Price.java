package com.example.upbitautotrade.model;

import com.google.gson.annotations.SerializedName;

public class Price {
    @SerializedName("currency")
    private String currency;

    @SerializedName("price_unit")
    private String price_unit;

    @SerializedName("min_total")
    private String min_total;

    public String getCurrency() {
        return currency;
    }

    public String getPrice_unit() {
        return price_unit;
    }

    public String getMin_total() {
        return min_total;
    }
}
