package com.example.upbitautotrade.api;

import com.example.upbitautotrade.model.Accounts;
import com.example.upbitautotrade.model.Chance;
import com.example.upbitautotrade.model.MarketInfo;
import com.example.upbitautotrade.model.Ticker;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
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
}
