package com.example.upbitautotrade.model;

public class Post {

    String market;
    String side;
    String volume;
    String price;
    String ord_type;
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
