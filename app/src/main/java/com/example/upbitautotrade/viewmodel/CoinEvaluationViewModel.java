package com.example.upbitautotrade.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.upbitautotrade.api.UpBitFetcher;
import com.example.upbitautotrade.model.Candle;
import com.example.upbitautotrade.model.DayCandle;
import com.example.upbitautotrade.model.MonthCandle;
import com.example.upbitautotrade.model.OrderInfo;
import com.example.upbitautotrade.model.Post;
import com.example.upbitautotrade.model.ResponseOrder;
import com.example.upbitautotrade.model.Ticker;
import com.example.upbitautotrade.model.TradeInfo;
import com.example.upbitautotrade.model.WeekCandle;

import java.util.List;

import retrofit2.http.Query;

public class CoinEvaluationViewModel extends UpBitViewModel{
    private final String TAG = "CoinEvaluationViewModel";

    private final MutableLiveData<String> mSearchTickerInfo;
    private final LiveData<List<Ticker>> mResultTickerInfo;

    private final MutableLiveData<CandleInput> mSearchMinCandleInfo;
    private final LiveData<List<Candle>> mResultMinCandleInfo;

    private final MutableLiveData<CandleInput> mSearchDayCandleInfo;
    private final LiveData<List<DayCandle>> mResultDayCandleInfo;

    private final MutableLiveData<CandleInput> mSearchWeekCandleInfo;
    private final LiveData<List<WeekCandle>> mResultWeekCandleInfo;

    private final MutableLiveData<CandleInput> mSearchMonthCandleInfo;
    private final LiveData<List<MonthCandle>> mResultMonthCandleInfo;

    private final MutableLiveData<TradeInput> mSearchTradeInfo;
    private final LiveData<List<TradeInfo>> mResultTradeInfo;

    private final MutableLiveData<Post> mPostOrderInfo;
    private final LiveData<ResponseOrder> mPostOrderResultInfo;

    private final MutableLiveData<String> mSearchOrderInfo;
    private final LiveData<ResponseOrder> mSearchOrderResultInfo;

    private final MutableLiveData<String> mDeleteOrderInfo;
    private final LiveData<ResponseOrder> mDeleteOrderResultInfo;

    public CoinEvaluationViewModel(Application application) {
        super(application);

        mSearchTickerInfo = new MutableLiveData<>();
        mResultTickerInfo = Transformations.switchMap(
                mSearchTickerInfo, input -> mUpBitFetcher.getTicker(input)
        );

        mSearchMinCandleInfo = new MutableLiveData<>();
        mResultMinCandleInfo = Transformations.switchMap(
                mSearchMinCandleInfo, input ->
                        mUpBitFetcher.getMinCandleInfo(
                                input.unit,
                                input.marketId,
                                input.to,
                                input.count)
        );

        mSearchDayCandleInfo = new MutableLiveData<>();
        mResultDayCandleInfo = Transformations.switchMap(
                mSearchDayCandleInfo, input ->
                        mUpBitFetcher.getDayCandleInfo(
                                input.marketId,
                                input.to,
                                input.count,
                                input.convertingPriceUnit
                        )
        );

        mSearchWeekCandleInfo = new MutableLiveData<>();
        mResultWeekCandleInfo = Transformations.switchMap(
                mSearchWeekCandleInfo, input ->
                        mUpBitFetcher.getWeekCandleInfo(
                                input.marketId,
                                input.to,
                                input.count
                        )
        );

        mSearchMonthCandleInfo = new MutableLiveData<>();
        mResultMonthCandleInfo = Transformations.switchMap(
                mSearchMonthCandleInfo, input ->
                        mUpBitFetcher.getMonthCandleInfo(
                                input.marketId,
                                input.to,
                                input.count
                        )
        );

        mSearchTradeInfo = new MutableLiveData<>();
        mResultTradeInfo = Transformations.switchMap(
                mSearchTradeInfo, input ->
                        mUpBitFetcher.getTradeInfo(
                                input.marketId,
                                input.to,
                                input.count,
                                input.cursor,
                                input.daysAgo
                        )
        );

        mPostOrderInfo = new MutableLiveData<>();
        mPostOrderResultInfo = Transformations.switchMap(
                mPostOrderInfo, input ->
                        mUpBitFetcher.postOrderInfo(input)
        );

        mSearchOrderInfo = new MutableLiveData<>();
        mSearchOrderResultInfo = Transformations.switchMap(
                mSearchOrderInfo, input ->
                        mUpBitFetcher.searchOrderInfo(input)
        );

        mDeleteOrderInfo = new MutableLiveData<>();
        mDeleteOrderResultInfo = Transformations.switchMap(
                mDeleteOrderInfo, input ->
                        mUpBitFetcher.deleteOrderInfo(input)
        );
    }

    @Override
    protected void initFetcher(Context context) {
        mUpBitFetcher = new UpBitFetcher(new UpBitFetcher.ConnectionState() {
            @Override
            public void onConnection(boolean isConnect) {

            }

            @Override
            public void postError(String uuid) {
                mPostErrorListener.postError(uuid);
            }
        });
    }

    public void setKey(String accessKey, String secretKey) {
        if (mUpBitFetcher != null) {
            mUpBitFetcher.makeRetrofit(accessKey, secretKey);
        }
    }

    public LiveData<List<Ticker>> getResultTickerInfo() {
        return mResultTickerInfo;
    }

    public void searchTickerInfo(String markerId) {
        mSearchTickerInfo.setValue(markerId);
    }


    public void searchMinCandleInfo(int unit, String marketId, String to, int count) {
        CandleInput input = new CandleInput(unit, marketId, to, count);
        mSearchMinCandleInfo.setValue(input);
    }

    public LiveData<List<Candle>> getMinCandleInfo() {
        return mResultMinCandleInfo;
    }

    public void searchDayCandleInfo(String marketId, String to, int count, String convertingPriceUnit) {
        CandleInput input = new CandleInput(marketId, to, count, convertingPriceUnit);
        mSearchDayCandleInfo.setValue(input);
    }

    public LiveData<List<DayCandle>> getDayCandleInfo() {
        return mResultDayCandleInfo;
    }


    public void searchWeekCandleInfo(String marketId, String to, int count) {
        CandleInput input = new CandleInput(marketId, to, count);
        mSearchWeekCandleInfo.setValue(input);
    }

    public LiveData<List<WeekCandle>> getWeekCandleInfo() {
        return mResultWeekCandleInfo;
    }


    public void searchMonthCandleInfo(String marketId, String to, int count) {
        CandleInput input = new CandleInput(marketId, to, count);
        mSearchMonthCandleInfo.setValue(input);
    }

    public LiveData<List<MonthCandle>> getMonthCandleInfo() {
        return mResultMonthCandleInfo;
    }

    public void searchTradeInfo(String marketId, String to, int count, String cursor, int daysAgo) {
        TradeInput input = new TradeInput(marketId, to, count, cursor, daysAgo);
        mSearchTradeInfo.setValue(input);
    }

    public LiveData<List<TradeInfo>> getTradeInfo() {
        return mResultTradeInfo;
    }

    public void postOrderInfo(String marketId, String side, String volume, String price, String ord_type, String identifier) {
        Post post = new Post(marketId, side, volume, price, ord_type, identifier);
        mPostOrderInfo.setValue(post);
    }

    public LiveData<ResponseOrder> getPostOrderInfo() {
        return mPostOrderResultInfo;
    }

    public void searchOrderInfo(String uuid) {
        mSearchOrderInfo.setValue(uuid);
    }

    public LiveData<ResponseOrder> getSearchOrderInfo() {
        return mSearchOrderResultInfo;
    }

    public void deleteOrderInfo(String uuid) {
        mDeleteOrderInfo.setValue(uuid);
    }

    public LiveData<ResponseOrder> getDeleteOrderInfo() {
        return mDeleteOrderResultInfo;
    }


    private class CandleInput {
        int unit;
        String marketId;
        String to;
        int count;
        String convertingPriceUnit;

        public CandleInput(String marketId, String to, int count) {
            this.marketId = marketId;
            this.to = to;
            this.count = count;
        }

        public CandleInput(int unit, String marketId, String to, int count) {
            this.unit = unit;
            this.marketId = marketId;
            this.to = to;
            this.count = count;
        }

        public CandleInput(String marketId, String to, int count, String convertingPriceUnit) {
            this.unit = unit;
            this.marketId = marketId;
            this.to = to;
            this.count = count;
            this.convertingPriceUnit = convertingPriceUnit;
        }
    }

    private class TradeInput {
        String marketId;
        String to;
        int count;
        String cursor;
        int daysAgo;

        public TradeInput(String marketId, String to, int count, String cursor, int daysAgo) {
            this.marketId = marketId;
            this.to = to;
            this.count = count;
            this.cursor = cursor;
            this.daysAgo = daysAgo;
        }

        public TradeInput(String marketId, int count, String cursor, int daysAgo) {
            this.marketId = marketId;
            this.count = count;
            this.cursor = cursor;
            this.daysAgo = daysAgo;
        }

        public TradeInput(String marketId, String to, int count, String cursor) {
            this.marketId = marketId;
            this.to = to;
            this.count = count;
            this.cursor = cursor;
        }

        public TradeInput(String marketId, int count, String cursor) {
            this.marketId = marketId;
            this.count = count;
            this.cursor = cursor;
        }
    }
}
