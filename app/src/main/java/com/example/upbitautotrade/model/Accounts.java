package com.example.upbitautotrade.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Accounts {
    @SerializedName("currency")
    private String currency;

    @SerializedName("balance")
    private String balance;

    @SerializedName("locked")
    private String locked;

    @SerializedName("avg_buy_price")
    private String avgBuyPrice;

    @SerializedName("avg_buy_price_modified")
    private boolean avgBuyPriceModified;

    private String getUnitCurrency() {
        return unitCurrency;
    }

    @SerializedName("unit_currency")
    private String unitCurrency;

    @Override
    public String toString() {
        return "Accounts{" +
                "currency='" + currency + '\'' +
                ", balance='" + balance + '\'' +
                ", locked='" + locked + '\'' +
                ", avgBuyPrice='" + avgBuyPrice + '\'' +
                ", avgBuyPriceModified=" + avgBuyPriceModified +
                ", unitCurrency='" + unitCurrency + '\'' +
                '}';
    }

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
}
