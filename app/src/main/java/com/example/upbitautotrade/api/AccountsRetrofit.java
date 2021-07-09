package com.example.upbitautotrade.api;

import android.content.Context;
import android.util.Log;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.upbitautotrade.UpBitLogInPreferences;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class AccountsRetrofit extends DefaultRetrofit{
    private static final String TAG = "AccountsRetrofit";

    @Override
    protected String getAuthToken() {
        if (mAccessKey == null || mSecretKey == null) {
            return null;
        }
        Log.d(TAG, "[DEBUG] getAuthToken -mAccessKey: "+mAccessKey+" mSecretKey: "+mSecretKey);
        Algorithm algorithm = Algorithm.HMAC256(mSecretKey);
        String jwtToken = JWT.create()
                .withClaim("access_key", mAccessKey)
                .withClaim("nonce", UUID.randomUUID().toString())
                .sign(algorithm);

        String authenticationToken = "Bearer " + jwtToken;
        return authenticationToken;
    }

    @Override
    public void setParam(String param1, String param2, String param3) {
        mAccessKey = param1;
        mSecretKey = param2;
    }
}
