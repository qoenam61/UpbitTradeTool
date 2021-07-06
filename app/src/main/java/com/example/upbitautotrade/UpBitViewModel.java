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

    public UpBitViewModel(Application application) {
        super(application);
        mUpBitFetcher = new UpBitFetcher();
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

    public void searchAccountsInfo() {
        Log.d(TAG, "[DEBUG] searchAccountsInfo: ");
        mSearchAccountsInfo.setValue("account");
    }

    public LiveData<List<Accounts>> getAccountsInfo() {
        Log.d(TAG, "[DEBUG] getAccountsInfo: ");
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
        mUpBitFetcher.setAccessKey(accessKey);
        mUpBitFetcher.setSecretKey(secretKey);
    }
}
