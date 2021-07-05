package com.example.upbitautotrade.api;

import com.example.upbitautotrade.model.Accounts;
import com.example.upbitautotrade.model.Chance;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UpBitApi {
    @GET("/v1/accounts")
    Call<Accounts> getAccounts();

    @GET("/v1/orders/chance")
    Call<Chance> getOrdersChance(@Query("marketId") String marketId);
}
