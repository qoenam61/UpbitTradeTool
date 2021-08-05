package com.example.upbitautotrade.utils;

import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_ACCOUNTS_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_CHANCE_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_DAY_CANDLE_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_MARKETS_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_MIN_CANDLE_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_MONTH_CANDLE_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_TICKER_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_TRADE_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_WEEK_CANDLE_INFO;

public class Item {

    private final long PERIODIC_TIME_10 = 10;
    private final long PERIODIC_TIME_45 = 45;
    private final long PERIODIC_TIME_50 = 50;
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

    public Item(int type, String key) {
        this.type = type;
        this.key = key;
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
            case UPDATE_MIN_CANDLE_INFO:
            case UPDATE_DAY_CANDLE_INFO:
            case UPDATE_WEEK_CANDLE_INFO:
            case UPDATE_MONTH_CANDLE_INFO:
                sleepTime = PERIODIC_TIME_45;
            default:
                break;
        }
        return sleepTime;
    }
}