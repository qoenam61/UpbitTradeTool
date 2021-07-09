package com.example.upbitautotrade.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Market {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("order_types")
    private ArrayList<String> order_types;

    @SerializedName("order_sides")
    private ArrayList<String> order_sides;

    @SerializedName("bid")
    private Price bid;

    @SerializedName("ask")
    private Price ask;

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

    public ArrayList<String> getOrder_types() {
        return order_types;
    }

    public ArrayList<String> getOrder_sides() {
        return order_sides;
    }

    public Price getBid() {
        return bid;
    }

    public Price getAsk() {
        return ask;
    }

    public String getMax_total() {
        return max_total;
    }

    public String getState() {
        return state;
    }
}
