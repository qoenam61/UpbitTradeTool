package com.example.upbitautotrade.model;

import com.google.gson.annotations.SerializedName;

public class Post {

    @SerializedName("market")
    String market;

    @SerializedName("side")
    String side;

    @SerializedName("volume")
    String volume;

    @SerializedName("price")
    String price;

    @SerializedName("ord_type")
    String ord_type;

    @SerializedName("identifier")
    String identifier;

    public Post(String marketId, String side, String volume, String price, String ord_type, String identifier) {
        this.market = marketId;
        this.side = side;
        this.volume = volume;
        this.price = price;
        this.ord_type = ord_type;
        this.identifier = identifier;
    }


    public String getMarketId() {
        return market;
    }

    public String getSide() {
        return side;
    }

    public String getVolume() {
        return volume;
    }

    public String getPrice() {
        return price;
    }

    public String getOrdType() {
        return ord_type;
    }

    public String getIdentifier() {
        return identifier;
    }

/*
    @Override
    public String toString() {
        return "POST {" +
                "market='" + market + '\'' +
                ", side='" + side + '\'' +
                ", volume='" + volume + '\'' +
                ", price='" + price + '\'' +
                ", ord_type=" + ord_type + '\'' +
                ", identifier='" + identifier + '\'' +
                '}';
    }
*/

}
