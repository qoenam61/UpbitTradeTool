package com.example.upbitautotrade.utils;

import com.google.gson.annotations.SerializedName;

import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_ACCOUNTS_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_CHANCE_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_DAY_CANDLE_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_DELETE_ORDER_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_MARKETS_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_MIN_CANDLE_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_MONTH_CANDLE_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_POST_ORDER_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_SEARCH_ORDER_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_TICKER_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_TRADE_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_WEEK_CANDLE_INFO;

public class Item {

    private final long PERIODIC_TIME_10 = 10;
    private final long PERIODIC_TIME_45 = 45;
    private final long PERIODIC_TIME_50 = 50;
    private final long PERIODIC_TIME_65 = 65;
    private final long PERIODIC_TIME_70 = 70;
    private final long PERIODIC_TIME_80 = 80;
    private final long PERIODIC_TIME_100 = 100;
    private final long PERIODIC_TIME_150 = 150;
    private final long PERIODIC_TIME_500 = 500;
    private final long PERIODIC_TIME_1000 = 1000;

    int type;
    String key;
    int count = 0;
    int unit;
    int daysAgo = -1;
    String to = null;
    String cursor = null;
    String priceUnit = null;

    String side = null;;
    String volume = null;;
    String price = null;;
    String ord_type = null;;
    String identifier = null;;


    public Item(int type, String key) {
        this.type = type;
        this.key = key;
    }

    public Item(int type, String key, String identifier) {
        this.type = type;
        this.key = key;
        this.identifier = identifier;
    }

    public Item(int type, String key, int count, int unit, int daysAgo, String to, String cursor, String priceUnit) {
        this.type = type;
        this.key = key;
        this.count = count;
        this.unit = unit;
        this.daysAgo = daysAgo;
        this.to = to;
        this.cursor = cursor;
        this.priceUnit = priceUnit;
    }

    public Item(int type, String key, String side, String volume, String price, String ord_type, String identifier) {
        this.type = type;
        this.key = key;
        this.side = side;
        this.volume = volume;
        this.price = price;
        this.ord_type = ord_type;
        this.identifier = identifier;
    }



    public long getSleepTime() {
        long sleepTime = 0;
        switch (type) {
            case UPDATE_MARKETS_INFO:
                sleepTime = PERIODIC_TIME_10;
                break;
            case UPDATE_CHANCE_INFO:
            case UPDATE_TICKER_INFO:
            case UPDATE_TRADE_INFO:
            case UPDATE_ACCOUNTS_INFO:

            case UPDATE_POST_ORDER_INFO:
            case UPDATE_SEARCH_ORDER_INFO:
            case UPDATE_DELETE_ORDER_INFO:
//                sleepTime = PERIODIC_TIME_100;
////                sleepTime = PERIODIC_TIME_80;
////                sleepTime = PERIODIC_TIME_65;
//                break;
            case UPDATE_MIN_CANDLE_INFO:
            case UPDATE_DAY_CANDLE_INFO:
            case UPDATE_WEEK_CANDLE_INFO:
            case UPDATE_MONTH_CANDLE_INFO:
                sleepTime = PERIODIC_TIME_150;
                break;
            default:
                sleepTime = PERIODIC_TIME_70;
        }
        return sleepTime;
    }
}