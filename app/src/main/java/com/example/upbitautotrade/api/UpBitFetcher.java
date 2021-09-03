package com.example.upbitautotrade.api;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.upbitautotrade.model.Candle;
import com.example.upbitautotrade.model.DayCandle;
import com.example.upbitautotrade.model.MarketInfo;
import com.example.upbitautotrade.model.MonthCandle;
import com.example.upbitautotrade.model.Post;
import com.example.upbitautotrade.model.ResponseOrder;
import com.example.upbitautotrade.model.Ticker;
import com.example.upbitautotrade.model.TradeInfo;
import com.example.upbitautotrade.model.WeekCandle;
import com.example.upbitautotrade.model.Accounts;
import com.example.upbitautotrade.model.Chance;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpBitFetcher {
    private static final String TAG = "UpBitFetcher";


    private final ConnectionState mListener;

    private Activity mActivity;

    private String mAccessKey;
    private String mSecretKey;

    public interface UpBitCallback<T> {
        void onSuccess(T response);
        void onFailure(Throwable t);
    }

    public interface ConnectionState {
        void onConnection(boolean isConnect);
        void deleteError(String uuid);
        void shortMoney(String uuid, String type);
    }

    private AccountsRetrofit mAccountsRetrofit;
    private ChanceRetrofit mChanceRetrofit;
    private SearchOrderRetrofit mSearchOrderRetrofit;
    private TickerRetrofit mTickerRetrofit;
    private OrderRetrofit mOrderRetrofit;
    private MutableLiveData<Throwable> mErrorLiveData;

    public UpBitFetcher(ConnectionState listener) {
        mListener = listener;
        mErrorLiveData = new MutableLiveData<>();
    }

    public void makeRetrofit(String accessKey, String secretKey) {
        mAccountsRetrofit = new AccountsRetrofit(accessKey, secretKey);
        mAccountsRetrofit.makeUpBitApi();
        mChanceRetrofit = new ChanceRetrofit(accessKey, secretKey);
        mChanceRetrofit.makeUpBitApi();
        mTickerRetrofit = new TickerRetrofit(accessKey, secretKey);
        mTickerRetrofit.makeUpBitApi();
        mOrderRetrofit = new OrderRetrofit(accessKey, secretKey);
        mOrderRetrofit.makeUpBitApi();
        mSearchOrderRetrofit = new SearchOrderRetrofit(accessKey, secretKey);
        mSearchOrderRetrofit.makeUpBitApi();
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    public MutableLiveData<Throwable> getErrorLiveData() {
        return mErrorLiveData;
    }

    public LiveData<List<Accounts>> getAccounts(boolean isLogIn) {
        MutableLiveData<List<Accounts>> result = new MutableLiveData<>();
        if (mAccountsRetrofit == null) {
            return null;
        }
        Call<List<Accounts>> call = mAccountsRetrofit.getUpBitApi().getAccounts();
        call.enqueue(new Callback<List<Accounts>>() {
            @Override
            public void onResponse(Call<List<Accounts>> call, Response<List<Accounts>> response) {
                Log.d(TAG, "onResponse: ");
                if (response.body() != null) {
                    if (isLogIn) {
                        if (mListener != null) {
                            mListener.onConnection(true);
                        }
                    }
                    result.setValue(response.body());
                } else {
                    if (isLogIn) {
                        if (mListener != null) {
                            mListener.onConnection(false);
                        }
                    }
                }
                if (!response.isSuccessful()) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.w(TAG, "onResponse getAccounts -toString: " + call.toString()
                                + " code: " + response.code()
                                + " headers: " + response.headers()
                                + " raw: " + response.raw()
                                + " jObjError: " + (jObjError != null ? jObjError : "NULL")
                        );
                        if (mActivity != null) {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mActivity, jObjError.toString(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        } catch(JSONException e){
                            e.printStackTrace();
                        } catch(IOException e){
                            e.printStackTrace();
                        }
                    }
                }

            @Override
            public void onFailure(Call<List<Accounts>> call, Throwable t) {
                Log.w(TAG, "onFailure: "+t);
                mErrorLiveData.setValue(t);
                if (isLogIn) {
                    if (mListener != null) {
                        mListener.onConnection(false);
                    }
                }
            }
        });
        return result;
    }

    public LiveData<List<Ticker>> getTicker(String marketId) {
        if (mTickerRetrofit == null) {
            return null;
        }
        MutableLiveData<List<Ticker>> result = new MutableLiveData<>();
        Call<List<Ticker>> call = mTickerRetrofit.getUpBitApi().getTicker(marketId);
        call.enqueue(new Callback<List<Ticker>>() {
            @Override
            public void onResponse(Call<List<Ticker>> call, Response<List<Ticker>> response) {
                if (response.body() != null) {
                    result.setValue(response.body());
                }
//                if (!response.isSuccessful()) {
//                    try {
//                        JSONObject jObjError = new JSONObject(response.errorBody().string());
//                        Log.d(TAG, "[DEBUG] onResponse getTicker -toString: " + call.toString()
//                                + " code: " + response.code()
//                                + " headers: " + response.headers()
//                                + " raw: " + response.raw()
//                                + " jObjError: " + jObjError
//                        );
//                        if (mActivity != null) {
//                            mActivity.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(mActivity, jObjError.toString(), Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        }
//                    } catch(JSONException e){
//                        e.printStackTrace();
//                    } catch(IOException e){
//                        e.printStackTrace();
//                    }
//                }
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
        if (mChanceRetrofit == null) {
            return null;
        }
        mChanceRetrofit.setParam(marketId, null, null);
        MutableLiveData<Chance> result = new MutableLiveData<>();
        Call<Chance> call = mChanceRetrofit.getUpBitApi().getOrdersChance(marketId);
        call.enqueue(new Callback<Chance>() {
            @Override
            public void onResponse(Call<Chance> call, Response<Chance> response) {
                if (response.body() != null) {
                    result.setValue(response.body());
                }
                if (!response.isSuccessful()) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.w(TAG, "[DEBUG] onResponse getOrdersChance -toString: " + call.toString()
                                + " code: " + response.code()
                                + " headers: " + response.headers()
                                + " raw: " + response.raw()
                                + " jObjError: " + (jObjError != null ? jObjError : "NULL")
                        );
                        if (mActivity != null) {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mActivity, jObjError.toString(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } catch(JSONException e){
                        e.printStackTrace();
                    } catch(IOException e){
                        e.printStackTrace();
                    }
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
        if (mTickerRetrofit == null) {
            return null;
        }
        MutableLiveData<List<MarketInfo>> result = new MutableLiveData<>();
        Call<List<MarketInfo>> call = mTickerRetrofit.getUpBitApi().getMarketInfo(isDetails);
        call.enqueue(new Callback<List<MarketInfo>>() {
            @Override
            public void onResponse(Call<List<MarketInfo>> call, Response<List<MarketInfo>> response) {
                if (response.body() != null) {
                    result.setValue(response.body());
                }
                if (!response.isSuccessful()) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.w(TAG, "onResponse getMarketInfo -toString: " + call.toString()
                                + " code: " + response.code()
                                + " headers: " + response.headers()
                                + " raw: " + response.raw()
                                + " jObjError: " + (jObjError != null ? jObjError : "NULL")
                        );
                        if (mActivity != null) {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mActivity, jObjError.toString(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } catch(JSONException e){
                        e.printStackTrace();
                    } catch(IOException e){
                        e.printStackTrace();
                    }
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
        if (mTickerRetrofit == null) {
            return null;
        }
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
                if (!response.isSuccessful()) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.w(TAG, "onResponse getMinCandleInfo -toString: " + call.toString()
                                + " code: " + response.code()
                                + " headers: " + response.headers()
                                + " raw: " + response.raw()
                                + " jObjError: " + (jObjError != null ? jObjError : "NULL")
                        );
                        if (mActivity != null) {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mActivity, jObjError.toString(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } catch(JSONException e){
                        e.printStackTrace();
                    } catch(IOException e){
                        e.printStackTrace();
                    }
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
        if (mTickerRetrofit == null) {
            return null;
        }
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
                if (!response.isSuccessful()) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.w(TAG, "onResponse getDayCandleInfo -toString: " + call.toString()
                                + " code: " + response.code()
                                + " headers: " + response.headers()
                                + " raw: " + response.raw()
                                + " jObjError: " + (jObjError != null ? jObjError : "NULL")
                        );
                        if (mActivity != null) {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mActivity, jObjError.toString(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } catch(JSONException e){
                        e.printStackTrace();
                    } catch(IOException e){
                        e.printStackTrace();
                    }
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
        if (mTickerRetrofit == null) {
            return null;
        }
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
                if (!response.isSuccessful()) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.w(TAG, "onResponse getWeekCandleInfo -toString: " + call.toString()
                                + " code: " + response.code()
                                + " headers: " + response.headers()
                                + " raw: " + response.raw()
                                + " jObjError: " + (jObjError != null ? jObjError : "NULL")
                        );
                        if (mActivity != null) {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mActivity, jObjError.toString(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } catch(JSONException e){
                        e.printStackTrace();
                    } catch(IOException e){
                        e.printStackTrace();
                    }
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
        if (mTickerRetrofit == null) {
            return null;
        }
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
                if (!response.isSuccessful()) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.w(TAG, "onResponse getMonthCandleInfo -toString: " + call.toString()
                                + " code: " + response.code()
                                + " headers: " + response.headers()
                                + " raw: " + response.raw()
                                + " jObjError: " + (jObjError != null ? jObjError : "NULL")
                        );
                        if (mActivity != null) {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mActivity, jObjError.toString(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } catch(JSONException e){
                        e.printStackTrace();
                    } catch(IOException e){
                        e.printStackTrace();
                    }
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
        if (mTickerRetrofit == null) {
            return null;
        }
        MutableLiveData<List<TradeInfo>> result = new MutableLiveData<>();
        Call<List<TradeInfo>> call;
        if (to != null && daysAgo > 0) {
            call = mTickerRetrofit.getUpBitApi().getTradeInfo(marketId, to, count, daysAgo);
//            call = mTickerRetrofit.getUpBitApi().getTradeInfo(marketId, to, count, cursor, daysAgo);
        } else if (to != null && daysAgo < 0) {
            call = mTickerRetrofit.getUpBitApi().getTradeInfo(marketId, to, count);
//            call = mTickerRetrofit.getUpBitApi().getTradeInfo(marketId, to, count, cursor);
        } else if (to == null && daysAgo > 0) {
            call = mTickerRetrofit.getUpBitApi().getTradeInfo(marketId, count, daysAgo);
//            call = mTickerRetrofit.getUpBitApi().getTradeInfo(marketId, count, cursor, daysAgo);
        } else {
            call = mTickerRetrofit.getUpBitApi().getTradeInfo(marketId, count);
//            call = mTickerRetrofit.getUpBitApi().getTradeInfo(marketId, count, cursor);
        }
        long startTime = System.currentTimeMillis();

        call.enqueue(new Callback<List<TradeInfo>>() {
            @Override
            public void onResponse(Call<List<TradeInfo>> call, Response<List<TradeInfo>> response) {
                if (response.body() != null) {
                    result.setValue(response.body());
                }
//                if (!response.isSuccessful()) {
//                    try {
//                        JSONObject jObjError = new JSONObject(response.errorBody().string());
//                        Log.d(TAG, "[DEBUG] onResponse getTradeInfo -toString: " + call.toString()
//                                + " code: " + response.code()
//                                + " headers: " + response.headers()
//                                + " raw: " + response.raw()
//                                + " jObjError: " + jObjError
//                        );
//                        if (mActivity != null) {
//                            mActivity.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(mActivity, jObjError.toString(), Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        }
//                    } catch(JSONException e){
//                        e.printStackTrace();
//                    } catch(IOException e){
//                        e.printStackTrace();
//                    }
//                }
            }

            @Override
            public void onFailure(Call<List<TradeInfo>> call, Throwable t) {
                Log.w(TAG, "onFailure: "+t);
                mErrorLiveData.setValue(t);
            }
        });
        return result;
    }

    public LiveData<ResponseOrder> postOrderInfo(Post post) {
        if (mOrderRetrofit == null || post == null) {
            return null;
        }

        String marketId = post.getMarketId();
        String side = post.getSide();
        String volume = post.getVolume();
        String price = post.getPrice();
        String ord_type = post.getOrdType();
        String identifier = post.getIdentifier();

        HashMap<String, String> params = new HashMap<>();
        if (marketId != null) {
            params.put("market", marketId);
        }
        if (side != null) {
            params.put("side", side);
        }

        if (volume != null) {
            params.put("volume", volume);
        }
        if (price != null) {
            params.put("price", price);
        }

        if (ord_type != null) {
            params.put("ord_type", ord_type);
        }
        if (identifier != null) {
            params.put("identifier", identifier);
        }

        mOrderRetrofit.setParam(params);

        MutableLiveData<ResponseOrder> result = new MutableLiveData<>();
        Call<ResponseOrder> call = mOrderRetrofit.getUpBitApi().postOrderInfo(params);
        call.enqueue(new Callback<ResponseOrder>() {
            @Override
            public void onResponse(Call<ResponseOrder> call, Response<ResponseOrder> response) {
                if(response.isSuccessful()) {
                    result.setValue(response.body());
                }
                else {
                    if (!response.isSuccessful()) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            if (jObjError != null) {
                                Log.w(TAG, "[DEBUG] onResponse postOrderInfo -toString: " + call.toString()
                                        + " code: " + response.code()
                                        + " headers: " + response.headers()
                                        + " raw: " + response.raw()
                                        + " jObjError: " + (jObjError != null ? jObjError : "NULL")
                                );
                                JSONObject errorObj = (JSONObject) jObjError.get("error");
                                if (response.code() == 400
                                        && errorObj != null
                                        && errorObj.get("name") != null && errorObj.get("name").equals("insufficient_funds_ask")) {
                                    if (mListener != null) {
                                        mListener.shortMoney(identifier, "ask");
                                    }
                                } else if (response.code() == 400
                                        && errorObj != null
                                        && errorObj.get("name") != null && errorObj.get("name").equals("insufficient_funds_bid")) {
                                    if (mListener != null) {
                                        mListener.shortMoney(identifier, "bid");
                                    }
                                }
                                if (mActivity != null) {
                                    mActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mActivity, jObjError.toString(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        } catch(JSONException e){
                            e.printStackTrace();
                        } catch(IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseOrder> call, Throwable t) {
                Log.w(TAG, "onFailure: "+t);
                mErrorLiveData.setValue(t);
            }
        });
        return result;
    }

    public LiveData<ResponseOrder> searchOrderInfo(String uuid) {
        if (mSearchOrderRetrofit == null) {
            return null;
        }

        HashMap<String, String> params = new HashMap<>();
        if (uuid != null) {
            params.put("uuid", uuid);
        }

        mSearchOrderRetrofit.setParam(params);

        MutableLiveData<ResponseOrder> result = new MutableLiveData<>();
        Call<ResponseOrder> call = mSearchOrderRetrofit.getUpBitApi().searchOrderInfo(uuid);
        call.enqueue(new Callback<ResponseOrder>() {
            @Override
            public void onResponse(Call<ResponseOrder> call, Response<ResponseOrder> response) {
                if (response.isSuccessful()) {
                    result.setValue(response.body());
                } else {
                    if (!response.isSuccessful()) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Log.w(TAG, "onResponse searchOrderInfo -toString: " + call.toString()
                                    + " code: " + response.code()
                                    + " headers: " + response.headers()
                                    + " raw: " + response.raw()
                                    + " jObjError: " + (jObjError != null ? jObjError : "NULL")
                            );
                            if (mActivity != null) {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mActivity, jObjError.toString(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        } catch(JSONException e){
                            e.printStackTrace();
                        } catch(IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseOrder> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t);
                mErrorLiveData.setValue(t);
            }
        });
        return result;
    }

    public LiveData<ResponseOrder> deleteOrderInfo(String uuid) {
        if (mSearchOrderRetrofit == null) {
            return null;
        }

        HashMap<String, String> params = new HashMap<>();
        if (uuid != null) {
            params.put("uuid", uuid);
        }

        mSearchOrderRetrofit.setParam(params);

        MutableLiveData<ResponseOrder> result = new MutableLiveData<>();
        Call<ResponseOrder> call = mSearchOrderRetrofit.getUpBitApi().deleteOrderInfo(uuid);
        call.enqueue(new Callback<ResponseOrder>() {
            @Override
            public void onResponse(Call<ResponseOrder> call, Response<ResponseOrder> response) {
                if (response.isSuccessful()) {
                    result.setValue(response.body());
                } else {
                    if (!response.isSuccessful()) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            if (jObjError != null) {
                                JSONObject errorObj = (JSONObject) jObjError.get("error");
                                Log.w(TAG, "[DEBUG] onResponse deleteOrderInfo -deleteError: " + call.toString()
                                        + " code: " + response.code()
                                        + " headers: " + response.headers()
                                        + " raw: " + response.raw()
                                        + " jObjError: " + (jObjError != null ? jObjError : "NULL")
                                );
                                if (response.code() == 404 && errorObj != null && errorObj.get("name") != null && errorObj.get("name").equals("order_not_found")) {
                                    if (mListener != null) {
                                        mListener.deleteError(uuid);
                                    }
                                } else if (response.code() == 400 && errorObj != null && errorObj.get("name") != null && errorObj.get("name").equals("insufficient_funds_ask")) {
                                    if (mListener != null) {
                                        mListener.shortMoney(uuid, "ask");
                                    }
                                } else if (response.code() == 400 && errorObj != null && errorObj.get("name") != null && errorObj.get("name").equals("insufficient_funds_bid")) {
                                    if (mListener != null) {
                                        mListener.shortMoney(uuid, "bid");
                                    }
                                }
                                if (mActivity != null) {
                                    mActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mActivity, jObjError.toString(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        } catch(JSONException e){
                            e.printStackTrace();
                        } catch(IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseOrder> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t);
                mErrorLiveData.setValue(t);
            }
        });
        return result;
    }
}
