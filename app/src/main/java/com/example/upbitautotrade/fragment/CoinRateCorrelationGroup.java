package com.example.upbitautotrade.fragment;

import com.example.upbitautotrade.model.DayCandle;

import java.util.Iterator;
import java.util.List;

public class CoinRateCorrelationGroup extends CoinCorrelationGroup {

    @Override
    protected void setCompareItem(List<DayCandle> candles, List<Double> comparePriceList) {
        Iterator<DayCandle> compareCandleIterator = candles.iterator();
        double beforePrice = 0;
        int i = 0;
        while (compareCandleIterator.hasNext()) {
            DayCandle candle = compareCandleIterator.next();
            double currentPrice = candle.getTradePrice().doubleValue();
            if (currentPrice == 0) {
                break;
            }
            if (i == 0) {
                beforePrice = currentPrice;
            } else {
                double changedPrice = currentPrice - beforePrice;
                double rate = changedPrice / currentPrice;
                comparePriceList.add(rate);
            }
            i++;
        }
    }

    @Override
    protected String getTitleName() {
        return "변화율 유사 그룹 선택";
    }


}
