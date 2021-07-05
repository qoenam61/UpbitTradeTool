package com.example.upbitautotrade.api;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.upbitautotrade.model.Accounts;
import com.example.upbitautotrade.model.Chance;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class UpBitFetcher {
    private static final String TAG = "UpBitFetcher";

    public interface UpBitCallback<T> {
        void onSuccess(T response);
        void onFailure(Throwable t);
    }

    private final UpBitApi mUpbitApi;

    private final MutableLiveData<Throwable> mErrorLiveData;

    public UpBitFetcher() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.upbit.com")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        mUpbitApi = retrofit.create(UpBitApi.class);

        mErrorLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<Throwable> getErrorLiveData() {
        return mErrorLiveData;
    }

    public LiveData<Accounts> getAccounts() {
        MutableLiveData<Accounts> result = new MutableLiveData<>();
        Call<Accounts> call = mUpbitApi.getAccounts();
        call.enqueue(new Callback<Accounts>() {
            @Override
            public void onResponse(Call<Accounts> call, Response<Accounts> response) {
                if (response.body() != null) {
                    result.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<Accounts> call, Throwable t) {
                mErrorLiveData.setValue(t);
            }
        });
        return result;
    }

    public LiveData<Chance> getOrdersChance(String marketId) {
        MutableLiveData<Chance> result = new MutableLiveData<>();
        Call<Chance> call = mUpbitApi.getOrdersChance(marketId);
        call.enqueue(new Callback<Chance>() {
            @Override
            public void onResponse(Call<Chance> call, Response<Chance> response) {
                if (response.body() != null) {
                    result.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<Chance> call, Throwable t) {
                mErrorLiveData.setValue(t);
            }
        });
        return result;
    }
}
