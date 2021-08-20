package com.example.upbitautotrade.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.upbitautotrade.UpBitLogInPreferences;
import com.example.upbitautotrade.api.UpBitFetcher;
import com.example.upbitautotrade.model.Accounts;
import com.example.upbitautotrade.model.Candle;
import com.example.upbitautotrade.model.Chance;
import com.example.upbitautotrade.model.Market;
import com.example.upbitautotrade.model.MarketInfo;
import com.example.upbitautotrade.model.Price;

import java.util.List;

public class UpBitViewModel extends AndroidViewModel {
    private final String TAG = "UpBitViewModel";

    public static final String LOGIN = "LogIn";

    private final LiveData<Throwable> mErrorLiveData;
    private final MutableLiveData<Boolean> mSearchAccountsInfo;
    private final LiveData<List<Accounts>> mResultAccountsInfo;
    private final MutableLiveData<Boolean> mSearchMarketsInfo;
    private final LiveData<List<MarketInfo>> mResultMarketsInfo;

    protected UpBitFetcher mUpBitFetcher;
    private boolean mIsSuccessfulConnection;
    private String mAccessKey = null;
    private String mSecretKey = null;

    private LoginState mListener;

    protected Activity mActivity;

    public interface LoginState {
        void onLoginState(boolean isLogin);
    }

    public UpBitViewModel(Application application) {
        super(application);
        initFetcher(application.getApplicationContext());
        mErrorLiveData = mUpBitFetcher.getErrorLiveData();

        mSearchAccountsInfo = new MutableLiveData<>();
        mResultAccountsInfo = Transformations.switchMap(
                mSearchAccountsInfo, input -> mUpBitFetcher.getAccounts(input)
        );

        mSearchMarketsInfo = new MutableLiveData<>();
        mResultMarketsInfo = Transformations.switchMap(
                mSearchMarketsInfo, input -> mUpBitFetcher.getMarketInfo(input)
        );
    }

    protected void initFetcher(Context context) {
        mUpBitFetcher = new UpBitFetcher(new UpBitFetcher.ConnectionState() {
            @Override
            public void onConnection(boolean isConnect) {
                mIsSuccessfulConnection = isConnect;
                if (isConnect) {
                    UpBitLogInPreferences.setStoredKey(context.getApplicationContext(), UpBitLogInPreferences.ACCESS_KEY, mAccessKey);
                    UpBitLogInPreferences.setStoredKey(context.getApplicationContext(), UpBitLogInPreferences.SECRET_KEY, mSecretKey);
                }
                mListener.onLoginState(isConnect);
            }
        });
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
        mUpBitFetcher.setActivity(activity);
    }

    public void setKey(String accessKey, String secretKey) {
        if (mUpBitFetcher != null) {
            mUpBitFetcher.makeRetrofit(accessKey, secretKey);
        }
        mAccessKey = accessKey;
        mSecretKey = secretKey;
    }

    public void setOnListener(LoginState listener) {
        mListener = listener;
    }

    public void searchAccountsInfo(boolean isLogIn) {
        mSearchAccountsInfo.setValue(isLogIn);
    }

    public LiveData<List<Accounts>> getAccountsInfo() {
        return mResultAccountsInfo;
    }

    public void searchMarketsInfo(boolean isDetails) {
        mSearchMarketsInfo.setValue(isDetails);
    }

    public LiveData<List<MarketInfo>> getMarketsInfo() {
        return mResultMarketsInfo;
    }

    public LiveData<Throwable> getErrorLiveData() {
        return mErrorLiveData;
    }
}
