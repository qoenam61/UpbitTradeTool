package com.example.upbitautotrade.api;

import com.example.upbitautotrade.model.Accounts;
import com.example.upbitautotrade.model.Candle;
import com.example.upbitautotrade.model.Chance;
import com.example.upbitautotrade.model.DayCandle;
import com.example.upbitautotrade.model.MarketInfo;
import com.example.upbitautotrade.model.MonthCandle;
import com.example.upbitautotrade.model.Post;
import com.example.upbitautotrade.model.ResponseOrder;
import com.example.upbitautotrade.model.Ticker;
import com.example.upbitautotrade.model.TradeInfo;
import com.example.upbitautotrade.model.WeekCandle;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UpBitApi {
    @GET("/v1/accounts")
    Call<List<Accounts>> getAccounts();

    @GET("/v1/orders/chance")
    Call<Chance> getOrdersChance(@Query("market") String marketId);

    @GET("/v1/ticker")
    Call<List<Ticker>> getTicker(@Query("markets") String marketId);

    @GET("/v1/market/all")
    Call<List<MarketInfo>> getMarketInfo(@Query("isDetails") boolean isDetails);

    @GET("/v1/candles/minutes/1")
    Call<List<Candle>> get1MinCandleInfo(@Query("market") String marketId,
                                        @Query("count") int count);


    @GET("/v1/candles/minutes/1")
    Call<List<Candle>> get1MinCandleInfo(@Query("market") String marketId,
                                        @Query("to") String to,
                                        @Query("count") int count);

    @GET("/v1/candles/days")
    Call<List<DayCandle>> getDayCandleInfo(@Query("market") String marketId,
                                           @Query("count") int count,
                                           @Query("convertingPriceUnit") String convertingPriceUnit);


    @GET("/v1/candles/days")
    Call<List<DayCandle>> getDayCandleInfo(@Query("market") String marketId,
                                           @Query("to") String to,
                                           @Query("count") int count,
                                           @Query("convertingPriceUnit") String convertingPriceUnit);

    @GET("/v1/candles/weeks")
    Call<List<WeekCandle>> getWeekCandleInfo(@Query("market") String marketId,
                                             @Query("to") String to,
                                             @Query("count") int count);

    @GET("/v1/candles/weeks")
    Call<List<WeekCandle>> getWeekCandleInfo(@Query("market") String marketId,
                                             @Query("count") int count);

    @GET("/v1/candles/months")
    Call<List<MonthCandle>> getMonthsCandleInfo(@Query("market") String marketId,
                                                @Query("count") int count);

    @GET("/v1/candles/months")
    Call<List<MonthCandle>> getMonthsCandleInfo(@Query("market") String marketId,
                                                @Query("to") String to,
                                                @Query("count") int count);


    @GET("/v1/trades/ticks")
    Call<List<TradeInfo>> getTradeInfo(@Query("market") String marketId,
                                       @Query("to") String to,
                                       @Query("count") int count,
//                                       @Query("cursor") String cursor,
                                       @Query("daysAgo") int daysAgo);


    @GET("/v1/trades/ticks")
    Call<List<TradeInfo>> getTradeInfo(@Query("market") String marketId,
                                       @Query("count") int count,
//                                       @Query("cursor") String cursor,
                                       @Query("daysAgo") int daysAgo);


    @GET("/v1/trades/ticks")
    Call<List<TradeInfo>> getTradeInfo(@Query("market") String marketId,
                                       @Query("to") String to,
                                       @Query("count") int count
//                                       @Query("cursor") String cursor
    );

    @GET("/v1/trades/ticks")
    Call<List<TradeInfo>> getTradeInfo(@Query("market") String marketId,
                                       @Query("count") int count
//                                       @Query("cursor") String cursor
    );

    @FormUrlEncoded
    @POST("/v1/orders")
    Call<ResponseOrder> postOrderInfo(@FieldMap Map<String, String> fields);

    @GET("/v1/order")
    Call<ResponseOrder> searchOrderInfo(@Query("uuid") String uuid);

    @DELETE("/v1/order")
    Call<ResponseOrder> deleteOrderInfo(@Query("uuid") String uuid);
}
