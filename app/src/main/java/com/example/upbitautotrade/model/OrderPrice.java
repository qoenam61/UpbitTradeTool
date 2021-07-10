package com.example.upbitautotrade.model;

import com.google.gson.annotations.SerializedName;

public class OrderPrice {
    @SerializedName("currency")
    String currency;

    @SerializedName("balance")
    Number balance;

    @SerializedName("locked")
    Number locked;

    @SerializedName("avg_buy_price")
    Number avgBuyPrice;

    @SerializedName("avg_buy_price_modified")
    boolean avgBuyPriceModified;

    @SerializedName("unit_currency")
    String unitCurrency;

    public String getCurrency() {
        return currency;
    }

    public Number getBalance() {
        return balance;
    }

    public Number getLocked() {
        return locked;
    }

    public Number getAvgBuyPrice() {
        return avgBuyPrice;
    }

    public boolean isAvgBuyPriceModified() {
        return avgBuyPriceModified;
    }

    public String getUnitCurrency() {
        return unitCurrency;
    }
}
