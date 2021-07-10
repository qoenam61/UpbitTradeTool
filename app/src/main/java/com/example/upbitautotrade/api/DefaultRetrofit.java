package com.example.upbitautotrade.api;

import android.util.Log;

import com.example.upbitautotrade.UpBitLogInPreferences;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public abstract class DefaultRetrofit {
    private String TAG = "DefaultRetrofit";

    protected String mAccessKey;
    protected String mSecretKey;
    private final UpBitApi mUpBitApi;

    public DefaultRetrofit() {
        mAccessKey = UpBitLogInPreferences.getStoredKey(UpBitLogInPreferences.ACCESS_KEY);
        mSecretKey = UpBitLogInPreferences.getStoredKey(UpBitLogInPreferences.SECRET_KEY);

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new HeaderInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.upbit.com")
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        mUpBitApi = retrofit.create(UpBitApi.class);
    }

    public class HeaderInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request origin = chain.request();
            Request request = changedRequest(origin);
            return chain.proceed(request);
        }
    }

    public Request changedRequest(Request origin) {
        return origin.newBuilder()
                .header("Content-Type", "application/json")
                .addHeader("Authorization", getAuthToken())
                .build();
    }

    abstract String getAuthToken();

    public abstract void setParam(String param1, String param2, String param3);

    public UpBitApi getUpBitApi() {
        return mUpBitApi;
    }
}
