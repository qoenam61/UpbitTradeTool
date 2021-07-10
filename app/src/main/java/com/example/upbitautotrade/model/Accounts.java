package com.example.upbitautotrade.model;

import com.google.gson.annotations.SerializedName;

public class Accounts {
    @SerializedName("currency")
    private String currency;

    @SerializedName("balance")
    private Number balance;

    @SerializedName("locked")
    private Number locked;

    @SerializedName("avg_buy_price")
    private Number avgBuyPrice;

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

    public int getTotalAmount() {
        return (int) ((getBalance().floatValue() + getLocked().floatValue()) * getAvgBuyPrice().floatValue());
    }
}
