package com.example.upbitautotrade.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.UUID;

import okhttp3.Request;

public class TickerRetrofit extends DefaultRetrofit {
    private String TAG = "TickerRetrofit";

    public TickerRetrofit(String accessKey, String secretKey) {
        super(accessKey, secretKey);
    }

    @Override
    String getAuthToken() {
        return null;
    }

    @Override
    public Request changedRequest(Request origin) {
        return origin.newBuilder()
                .header("Accept", "application/json")
                .build();
    }

    @Override
    public void setParam(String param1, String param2, String param3) {
    }
}
