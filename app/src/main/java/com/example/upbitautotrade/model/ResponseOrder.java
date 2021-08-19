package com.example.upbitautotrade.model;

import com.google.gson.annotations.SerializedName;

public class ResponseOrder {

    @SerializedName("uuid")
    String	uuid;

    @SerializedName("side")
    String	side;

    @SerializedName("ord_type")
    String	orderType;

    @SerializedName("price")
    Number	price;

    @SerializedName("avg_price")
    Number	avgPrice;

    @SerializedName("state")
    String	state;

    @SerializedName("market")
    String	market;

    @SerializedName("created_at")
    String	created_at;

    @SerializedName("volume")
    Number	volume;

    @SerializedName("remaining_volume")
    Number	remainingVolume;

    @SerializedName("reserved_fee")
    String	reservedFee;

    @SerializedName("remaining_fee")
    String remainingFee;

    @SerializedName("paid_fee")
    String	paid_fee;

    @SerializedName("locked")
    String	locked;

    @SerializedName("executed_volume")
    String	executedVolume;

    @SerializedName("trades_count")
    Integer	tradesCount;

    public String getUuid() {
        return uuid;
    }

    public String getSide() {
        return side;
    }

    public String getOrderType() {
        return orderType;
    }

    public Number getPrice() {
        return price;
    }

    public Number getAvgPrice() {
        return avgPrice;
    }

    public String getState() {
        return state;
    }

    public String getMarket() {
        return market;
    }

    public String getCreated_at() {
        return created_at;
    }

    public Number getVolume() {
        return volume;
    }

    public Number getRemainingVolume() {
        return remainingVolume;
    }

    public String getReservedFee() {
        return reservedFee;
    }

    public String getRemainingFee() {
        return remainingFee;
    }

    public String getPaid_fee() {
        return paid_fee;
    }

    public String getLocked() {
        return locked;
    }

    public String getExecutedVolume() {
        return executedVolume;
    }

    public Integer getTradesCount() {
        return tradesCount;
    }
}
