package com.example.upbitautotrade.viewmodel;

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
import com.example.upbitautotrade.model.Chance;
import com.example.upbitautotrade.model.Market;
import com.example.upbitautotrade.model.Price;

import java.util.List;

public class UpBitViewModel extends AndroidViewModel {
    private final String TAG = "UpBitViewModel";

    public static final String LOGIN = "LogIn";

    private final LiveData<Throwable> mErrorLiveData;
    private final MutableLiveData<String> mSearchAccountsInfo;
    private final LiveData<List<Accounts>> mResultAccountsInfo;

    protected UpBitFetcher mUpBitFetcher;
    private boolean mIsSuccessfulConnection;
    private String mAccessKey = null;
    private String mSecretKey = null;

    private LoginState mListener;

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
    }

    protected void initFetcher(Context context) {
        mUpBitFetcher = new UpBitFetcher(new UpBitFetcher.ConnectionState() {
            @Override
            public void onConnection(boolean isConnect) {
                mIsSuccessfulConnection = isConnect;
                Log.d(TAG, "getConnectionState -isConnect: "+isConnect);
                if (isConnect) {
                    UpBitLogInPreferences.setStoredKey(context.getApplicationContext(), UpBitLogInPreferences.ACCESS_KEY, mAccessKey);
                    UpBitLogInPreferences.setStoredKey(context.getApplicationContext(), UpBitLogInPreferences.SECRET_KEY, mSecretKey);
                }
                mListener.onLoginState(isConnect);
            }
        });
    }

    public void setOnListener(LoginState listener) {
        mListener = listener;
    }

    public void searchAccountsInfo(boolean isLogIn) {
        mSearchAccountsInfo.setValue(isLogIn ? LOGIN : null);
    }

    public boolean isSuccessfulConnection() {
        return mIsSuccessfulConnection;
    }

    public LiveData<List<Accounts>> getAccountsInfo() {
        return mResultAccountsInfo;
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
