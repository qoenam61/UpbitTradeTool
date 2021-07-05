package com.example.upbitautotrade.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Chance {
    @SerializedName("ask_fee")
    private String askFee;

    @SerializedName("market")
    private List<Market> market;

    @SerializedName("bid_account")
    private List<Accounts> bidAccount;

    @SerializedName("ask_account")
    private List<Accounts> askAccount;

    public List<Market> getMarketItems() {
        return market;
    }

    public List<Accounts> getBidItems() {
        return bidAccount;
    }

    public List<Accounts> getAskItems() {
        return askAccount;
    }
}
