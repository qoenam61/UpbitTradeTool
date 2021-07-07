package com.example.upbitautotrade.api;

import android.os.SystemClock;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.upbitautotrade.UpBitViewModel;
import com.example.upbitautotrade.model.Accounts;
import com.example.upbitautotrade.model.Chance;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class UpBitFetcher {
    private static final String TAG = "UpBitFetcher";


    private final ConnectionState mListener;
    private String mAccessKey;
    private String mSecretKey;

    public interface UpBitCallback<T> {
        void onSuccess(T response);
        void onFailure(Throwable t);
    }

    public interface ConnectionState {
        void onConnection(boolean isConnect);
    }

    private final UpBitApi mUpBitApi;

    private final MutableLiveData<Throwable> mErrorLiveData;

    public UpBitFetcher(ConnectionState listener) {
        mListener = listener;
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
        mErrorLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<Throwable> getErrorLiveData() {
        return mErrorLiveData;
    }

    public LiveData<List<Accounts>> getAccounts(String isLogIn) {
        MutableLiveData<List<Accounts>> result = new MutableLiveData<>();
        Call<List<Accounts>> call = mUpBitApi.getAccounts();
        call.enqueue(new Callback<List<Accounts>>() {
            @Override
            public void onResponse(Call<List<Accounts>> call, Response<List<Accounts>> response) {
                Log.d(TAG, "[DEBUG] onResponse: "+response.body()+" call: "+call.toString());
                if (response.body() != null) {
                    if (isLogIn != null && isLogIn.equals(UpBitViewModel.LOGIN)) {
                        mListener.onConnection(true);
                    }
                    result.setValue(response.body());
                } else {
                    if (isLogIn != null && isLogIn.equals(UpBitViewModel.LOGIN)) {
                        mListener.onConnection(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Accounts>> call, Throwable t) {
                Log.d(TAG, "[DEBUG] onFailure: "+t);
                mErrorLiveData.setValue(t);
                if (isLogIn != null && isLogIn.equals(UpBitViewModel.LOGIN)) {
                    mListener.onConnection(false);
                }
            }
        });
        return result;
    }

    public LiveData<Chance> getOrdersChance(String marketId) {
        MutableLiveData<Chance> result = new MutableLiveData<>();
        Call<Chance> call = mUpBitApi.getOrdersChance(marketId);
        Log.d(TAG, "[DEBUG] getOrdersChance: "+call.request());
        call.enqueue(new Callback<Chance>() {
            @Override
            public void onResponse(Call<Chance> call, Response<Chance> response) {
                Log.d(TAG, "[DEBUG] onResponse: "+response.body()+" call: "+call.toString());
                if (response.body() != null) {
                    result.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<Chance> call, Throwable t) {
                Log.d(TAG, "[DEBUG] onFailure: "+t);
                mErrorLiveData.setValue(t);
            }
        });
        return result;
    }

    public void setAccessKey(String accessKey) {
        mAccessKey = accessKey;
    }

    public void setSecretKey(String secretKey) {
        mSecretKey = secretKey;
    }

    class HeaderInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {

            Request origin = chain.request();
            Request request = origin.newBuilder()
                    .header("Content-Type", "application/json")
                    .addHeader("Authorization", getAuthToken())
                    .build();
            return chain.proceed(request);
        }
    }

    private String getAuthToken() {
        if (mAccessKey == null || mSecretKey == null) {
            return null;
        }
        Algorithm algorithm = Algorithm.HMAC256(mSecretKey);
        String jwtToken = JWT.create()
                .withClaim("access_key", mAccessKey)
                .withClaim("nonce", UUID.randomUUID().toString())
                .sign(algorithm);

        String authenticationToken = "Bearer " + jwtToken;
        return authenticationToken;
    }
}
