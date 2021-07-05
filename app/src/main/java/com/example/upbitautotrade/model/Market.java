package com.example.upbitautotrade.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Market {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("order_types")
    private String order_types;

    @SerializedName("order_sides")
    private String order_sides;

    @SerializedName("bid")
    private List<Price> bid;

    @SerializedName("ask")
    private List<Price> ask;

    @SerializedName("max_total")
    private String max_total;

    @SerializedName("state")
    private String state;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOrder_types() {
        return order_types;
    }

    public String getOrder_sides() {
        return order_sides;
    }

    public List<Price> getBid() {
        return bid;
    }

    public List<Price> getAsk() {
        return ask;
    }

    public String getMax_total() {
        return max_total;
    }

    public String getState() {
        return state;
    }
}
