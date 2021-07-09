package com.example.upbitautotrade.model;

import com.google.gson.annotations.SerializedName;

public class Chance {
    @SerializedName("ask_fee")
    private String askFee;

    @SerializedName("market")
    private Market market;

    @SerializedName("bid_account")
    private OrderPrice bidAccount;

    @SerializedName("ask_account")
    private OrderPrice askAccount;

    public String getAskFee() {
        return askFee;
    }

    public Market getMarket() {
        return market;
    }

    public OrderPrice getBidAccount() {
        return bidAccount;
    }

    public OrderPrice getAskAccount() {
        return askAccount;
    }
}
