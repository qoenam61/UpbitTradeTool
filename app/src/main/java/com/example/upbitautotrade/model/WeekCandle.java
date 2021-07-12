package com.example.upbitautotrade.model;

import com.google.gson.annotations.SerializedName;

public class WeekCandle extends Candle {
    @SerializedName("first_day_of_period")
    String firstDayOfPeriod;

    public String getFirstDayOfPeriod() {
        return firstDayOfPeriod;
    }
}
