package com.example.upbitautotrade.model;

import com.google.gson.annotations.SerializedName;

public class ResponseOrder {

    @SerializedName("uuid")
    String	uuid;

    @SerializedName("side")
    String	side;

    @SerializedName("ord_type")
    String	ord_type;

    @SerializedName("price")
    String	price;

    @SerializedName("avg_price")
    String	avg_price;

    @SerializedName("state")
    String	state;

    @SerializedName("market")
    String	market;

    @SerializedName("created_at")
    String	created_at;

    @SerializedName("volume")
    String	volume;

    @SerializedName("remaining_volume")
    String	remaining_volume;

    @SerializedName("reserved_fee")
    String	reserved_fee;

    @SerializedName("remaining_fee")
    String	remaining_fee;

    @SerializedName("paid_fee")
    String	paid_fee;

    @SerializedName("locked")
    String	locked;

    @SerializedName("executed_volume")
    String	executed_volume;

    @SerializedName("trades_count")
    Integer	trades_count;
}
