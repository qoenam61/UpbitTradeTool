package com.example.upbitautotrade.model;

import com.google.gson.annotations.SerializedName;

public class OrderPrice {
    @SerializedName("currency")
    String currency;

    @SerializedName("balance")
    String balance;

    @SerializedName("locked")
    String locked;

    @SerializedName("avg_buy_price")
    String avgBuyPrice;

    @SerializedName("avg_buy_price_modified")
    boolean avgBuyPriceModified;

    @SerializedName("unit_currency")
    String unitCurrency;

    public String getCurrency() {
        return currency;
    }

    public String getBalance() {
        return balance;
    }

    public String getLocked() {
        return locked;
    }

    public String getAvgBuyPrice() {
        return avgBuyPrice;
    }

    public boolean isAvgBuyPriceModified() {
        return avgBuyPriceModified;
    }

    public String getUnitCurrency() {
        return unitCurrency;
    }
}
