package com.example.upbitautotrade;

import android.app.Application;

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

    private final LiveData<Throwable> mErrorLiveData;

    private final MutableLiveData<String> mSearchAccountsInfo;
    private final LiveData<Accounts> mResultAccountsInfo;

    private final MutableLiveData<String> mSearchChanceInfo;
    private final LiveData<Chance> mResultChanceInfo;

    private final LiveData<List<Market>> mResultChanceMarketInfo;
    private final LiveData<List<Accounts>> mResultChanceBidInfo;
    private final LiveData<List<Accounts>> mResultChanceAskInfo;

    public UpBitViewModel(Application application) {
        super(application);
        UpBitFetcher fetcher = new UpBitFetcher();
        mErrorLiveData = fetcher.getErrorLiveData();

        mSearchAccountsInfo = new MutableLiveData<>();
        mResultAccountsInfo = Transformations.switchMap(
                mSearchAccountsInfo, input -> fetcher.getAccounts()
        );

        mSearchChanceInfo = new MutableLiveData<>();
        mResultChanceInfo = Transformations.switchMap(
                mSearchChanceInfo, input -> fetcher.getOrdersChance(input)
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

    public LiveData<Accounts> getAccountsInfo() {
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
}
