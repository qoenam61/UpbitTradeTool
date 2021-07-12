package com.example.upbitautotrade.api;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.upbitautotrade.model.Candle;
import com.example.upbitautotrade.model.DayCandle;
import com.example.upbitautotrade.model.MarketInfo;
import com.example.upbitautotrade.model.MonthCandle;
import com.example.upbitautotrade.model.Ticker;
import com.example.upbitautotrade.model.TradeInfo;
import com.example.upbitautotrade.model.WeekCandle;
import com.example.upbitautotrade.viewmodel.UpBitViewModel;
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

    private final AccountsRetrofit mAccountsRetrofit;
    private final ChanceRetrofit mChanceRetrofit;
    private final TickerRetrofit mTickerRetrofit;
    private final MutableLiveData<Throwable> mErrorLiveData;

    public UpBitFetcher(ConnectionState listener) {
        mListener = listener;
        mAccountsRetrofit = new AccountsRetrofit();
        mChanceRetrofit = new ChanceRetrofit();
        mTickerRetrofit = new TickerRetrofit();

        mErrorLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<Throwable> getErrorLiveData() {
        return mErrorLiveData;
    }

    public LiveData<List<Accounts>> getAccounts(boolean isLogIn) {
        MutableLiveData<List<Accounts>> result = new MutableLiveData<>();
        if (isLogIn) {
            mAccountsRetrofit.setParam(mAccessKey, mSecretKey, null);
        }
        Call<List<Accounts>> call = mAccountsRetrofit.getUpBitApi().getAccounts();
        call.enqueue(new Callback<List<Accounts>>() {
            @Override
            public void onResponse(Call<List<Accounts>> call, Response<List<Accounts>> response) {
                if (response.body() != null) {
                    if (isLogIn) {
                        mListener.onConnection(true);
                    }
                    result.setValue(response.body());
                } else {
                    if (isLogIn) {
                        mListener.onConnection(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Accounts>> call, Throwable t) {
                Log.w(TAG, "onFailure: "+t);
                mErrorLiveData.setValue(t);
                if (isLogIn) {
                    mListener.onConnection(false);
                }
            }
        });
        return result;
    }

    public LiveData<List<Ticker>> getTicker(String marketId) {
        MutableLiveData<List<Ticker>> result = new MutableLiveData<>();
        Call<List<Ticker>> call = mTickerRetrofit.getUpBitApi().getTicker(marketId);
        call.enqueue(new Callback<List<Ticker>>() {
            @Override
            public void onResponse(Call<List<Ticker>> call, Response<List<Ticker>> response) {
                if (response.body() != null) {
                    result.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Ticker>> call, Throwable t) {
                Log.w(TAG, "onFailure: "+t);
                mErrorLiveData.setValue(t);
            }
        });
        return result;
    }

    public LiveData<Chance> getOrdersChance(String marketId) {
        mChanceRetrofit.setParam(marketId, null, null);
        MutableLiveData<Chance> result = new MutableLiveData<>();
        Call<Chance> call = mChanceRetrofit.getUpBitApi().getOrdersChance(marketId);
        call.enqueue(new Callback<Chance>() {
            @Override
            public void onResponse(Call<Chance> call, Response<Chance> response) {
                if (response.body() != null) {
                    result.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<Chance> call, Throwable t) {
                Log.w(TAG, "onFailure: "+t);
                mErrorLiveData.setValue(t);
            }
        });
        return result;
    }

    public LiveData<List<MarketInfo>> getMarketInfo(boolean isDetails) {
        MutableLiveData<List<MarketInfo>> result = new MutableLiveData<>();
        Call<List<MarketInfo>> call = mTickerRetrofit.getUpBitApi().getMarketInfo(isDetails);
        call.enqueue(new Callback<List<MarketInfo>>() {
            @Override
            public void onResponse(Call<List<MarketInfo>> call, Response<List<MarketInfo>> response) {
                if (response.body() != null) {
                    result.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<MarketInfo>> call, Throwable t) {
                Log.w(TAG, "onFailure: "+t);
                mErrorLiveData.setValue(t);
            }
        });
        return result;
    }

    public LiveData<List<Candle>> getMinCandleInfo(int unit, String marketId, String to, int count) {
        MutableLiveData<List<Candle>> result = new MutableLiveData<>();
        Call<List<Candle>> call = to != null ?
                mTickerRetrofit.getUpBitApi().get1MinCandleInfo(marketId, to, count)
                : mTickerRetrofit.getUpBitApi().get1MinCandleInfo(marketId, count);
        call.enqueue(new Callback<List<Candle>>() {
            @Override
            public void onResponse(Call<List<Candle>> call, Response<List<Candle>> response) {
                if (response.body() != null) {
                    result.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Candle>> call, Throwable t) {
                Log.w(TAG, "onFailure: "+t);
                mErrorLiveData.setValue(t);
            }
        });
        return result;
    }

    public LiveData<List<DayCandle>> getDayCandleInfo(String marketId, String to, int count, String convertingPriceUnit) {
        MutableLiveData<List<DayCandle>> result = new MutableLiveData<>();
        Call<List<DayCandle>> call = to != null ?
                mTickerRetrofit.getUpBitApi().getDayCandleInfo(marketId, to, count, convertingPriceUnit)
                : mTickerRetrofit.getUpBitApi().getDayCandleInfo(marketId, count, convertingPriceUnit);
        call.enqueue(new Callback<List<DayCandle>>() {
            @Override
            public void onResponse(Call<List<DayCandle>> call, Response<List<DayCandle>> response) {
                if (response.body() != null) {
                    result.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<DayCandle>> call, Throwable t) {
                Log.w(TAG, "onFailure: "+t);
                mErrorLiveData.setValue(t);
            }
        });
        return result;
    }

    public LiveData<List<WeekCandle>> getWeekCandleInfo(String marketId, String to, int count) {
        MutableLiveData<List<WeekCandle>> result = new MutableLiveData<>();
        Call<List<WeekCandle>> call = to != null ?
                mTickerRetrofit.getUpBitApi().getWeekCandleInfo(marketId, to, count)
                : mTickerRetrofit.getUpBitApi().getWeekCandleInfo(marketId, count);
        call.enqueue(new Callback<List<WeekCandle>>() {
            @Override
            public void onResponse(Call<List<WeekCandle>> call, Response<List<WeekCandle>> response) {
                if (response.body() != null) {
                    result.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<WeekCandle>> call, Throwable t) {
                Log.w(TAG, "onFailure: "+t);
                mErrorLiveData.setValue(t);
            }
        });
        return result;
    }

    public LiveData<List<MonthCandle>> getMonthCandleInfo(String marketId, String to, int count) {
        MutableLiveData<List<MonthCandle>> result = new MutableLiveData<>();
        Call<List<MonthCandle>> call = to != null ?
                mTickerRetrofit.getUpBitApi().getMonthsCandleInfo(marketId, to, count)
                : mTickerRetrofit.getUpBitApi().getMonthsCandleInfo(marketId, count);
        call.enqueue(new Callback<List<MonthCandle>>() {
            @Override
            public void onResponse(Call<List<MonthCandle>> call, Response<List<MonthCandle>> response) {
                if (response.body() != null) {
                    result.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<MonthCandle>> call, Throwable t) {
                Log.w(TAG, "onFailure: "+t);
                mErrorLiveData.setValue(t);
            }
        });
        return result;
    }

    public LiveData<List<TradeInfo>> getTradeInfo(String marketId, String to, int count, String cursor, int daysAgo) {
        MutableLiveData<List<TradeInfo>> result = new MutableLiveData<>();
        Call<List<TradeInfo>> call;
        if (to != null && daysAgo < 0) {
            call = mTickerRetrofit.getUpBitApi().getTradeInfo(marketId, to, count, daysAgo);
//            call = mTickerRetrofit.getUpBitApi().getTradeInfo(marketId, to, count, cursor, daysAgo);
        } else if (to != null && daysAgo > 0) {
            call = mTickerRetrofit.getUpBitApi().getTradeInfo(marketId, to, count);
//            call = mTickerRetrofit.getUpBitApi().getTradeInfo(marketId, to, count, cursor);
        } else if (to == null && daysAgo > 0) {
            call = mTickerRetrofit.getUpBitApi().getTradeInfo(marketId, count, daysAgo);
//            call = mTickerRetrofit.getUpBitApi().getTradeInfo(marketId, count, cursor, daysAgo);
        } else {
            call = mTickerRetrofit.getUpBitApi().getTradeInfo(marketId, count);
//            call = mTickerRetrofit.getUpBitApi().getTradeInfo(marketId, count, cursor);
        }

        call.enqueue(new Callback<List<TradeInfo>>() {
            @Override
            public void onResponse(Call<List<TradeInfo>> call, Response<List<TradeInfo>> response) {
                if (response.body() != null) {
                    result.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<TradeInfo>> call, Throwable t) {
                Log.w(TAG, "onFailure: "+t);
                mErrorLiveData.setValue(t);
            }
        });
        return result;
    }

    public void setKey(String accessKey, String secretKey) {
        mAccessKey = accessKey;
        mSecretKey = secretKey;
    }
}
