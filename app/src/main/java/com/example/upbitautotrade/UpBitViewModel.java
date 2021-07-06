package com.example.upbitautotrade;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.upbitautotrade.api.UpBitFetcher;
import com.example.upbitautotrade.model.Accounts;
import com.example.upbitautotrade.model.Chance;
import com.example.upbitautotrade.model.Market;
import com.example.upbitautotrade.model.Price;

import java.util.List;

public class UpBitViewModel extends AndroidViewModel {
    private final String TAG = "UpBitViewModel";

    private final LiveData<Throwable> mErrorLiveData;

    private final MutableLiveData<String> mSearchAccountsInfo;
    private final LiveData<List<Accounts>> mResultAccountsInfo;

    private final MutableLiveData<String> mSearchChanceInfo;
    private final LiveData<Chance> mResultChanceInfo;

    private final LiveData<List<Market>> mResultChanceMarketInfo;
    private final LiveData<List<Accounts>> mResultChanceBidInfo;
    private final LiveData<List<Accounts>> mResultChanceAskInfo;
    private UpBitFetcher mUpBitFetcher;
    private boolean mIsSuccessfulConnection;
    private String mAccessKey = null;
    private String mSecretKey = null;

    private LoginState mListener;

    public interface LoginState {
        void onLoginState(boolean isLogin);
    }

    public UpBitViewModel(Application application) {
        super(application);
        mUpBitFetcher = new UpBitFetcher(new UpBitFetcher.ConnectionState() {
            @Override
            public void onConnection(boolean isConnect) {
                mIsSuccessfulConnection = isConnect;
                Log.d(TAG, "getConnectionState -isConnect: "+isConnect);
                if (isConnect) {
                    UpBitLogInPreferences.setStoredKey(application.getApplicationContext(), UpBitLogInPreferences.ACCESS_KEY, mAccessKey);
                    UpBitLogInPreferences.setStoredKey(application.getApplicationContext(), UpBitLogInPreferences.SECRET_KEY, mSecretKey);
                }
                mListener.onLoginState(isConnect);
            }
        });
        mErrorLiveData = mUpBitFetcher.getErrorLiveData();

        mSearchAccountsInfo = new MutableLiveData<>();
        mResultAccountsInfo = Transformations.switchMap(
                mSearchAccountsInfo, input -> mUpBitFetcher.getAccounts()
        );

        mSearchChanceInfo = new MutableLiveData<>();
        mResultChanceInfo = Transformations.switchMap(
                mSearchChanceInfo, input -> mUpBitFetcher.getOrdersChance(input)
        );

        mResultChanceMarketInfo = Transformations.map(
                mResultChanceInfo,
                Chance::getMarketItems);

        mResultChanceBidInfo = Transformations.map(
                mResultChanceInfo,
                Chance::getBidItems);

        mResultChanceAskInfo = Transformations.map(
                mResultChanceInfo,
                Chance::getAskItems);

    }

    public void setOnListener(LoginState listener) {
        mListener = listener;
    }

    public void searchAccountsInfo() {
        mSearchAccountsInfo.setValue(null);
    }

    public boolean isSuccessfulConnection() {
        return mIsSuccessfulConnection;
    }

    public LiveData<List<Accounts>> getAccountsInfo() {
        return mResultAccountsInfo;
    }

    public LiveData<List<Market>> getResultChanceMarketInfo() {
        return mResultChanceMarketInfo;
    }

    public LiveData<List<Accounts>> getResultChanceBidInfo() {
        return mResultChanceBidInfo;
    }

    public LiveData<List<Accounts>> getResultChanceAskInfo() {
        return mResultChanceAskInfo;
    }

    public LiveData<Throwable> getErrorLiveData() {
        return mErrorLiveData;
    }

    public void setKey(String accessKey, String secretKey) {
        if (mUpBitFetcher == null) {
            return;
        }
        mAccessKey = accessKey;
        mSecretKey = secretKey;

        mUpBitFetcher.setAccessKey(accessKey);
        mUpBitFetcher.setSecretKey(secretKey);
    }
}
