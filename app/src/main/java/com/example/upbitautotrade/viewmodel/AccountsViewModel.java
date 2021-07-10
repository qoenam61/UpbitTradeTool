package com.example.upbitautotrade.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.upbitautotrade.api.UpBitFetcher;
import com.example.upbitautotrade.model.Accounts;
import com.example.upbitautotrade.model.Chance;
import com.example.upbitautotrade.model.Market;
import com.example.upbitautotrade.model.Ticker;

import java.util.List;

public class AccountsViewModel extends UpBitViewModel{
    private final String TAG = "AccountsViewModel";

    private final MutableLiveData<String> mSearchChanceInfo;
    private final LiveData<Chance> mResultChanceInfo;

    private final MutableLiveData<String> mSearchTickerInfo;
    private final LiveData<Ticker> mResultTickerInfo;

/*    private final LiveData<List<Market>> mResultChanceMarketInfo;
    private final LiveData<List<Accounts>> mResultChanceBidInfo;
    private final LiveData<List<Accounts>> mResultChanceAskInfo;*/

    public AccountsViewModel(Application application) {
        super(application);
        mSearchChanceInfo = new MutableLiveData<>();
        mResultChanceInfo = Transformations.switchMap(
                mSearchChanceInfo, input -> mUpBitFetcher.getOrdersChance(input)
        );

        mSearchTickerInfo = new MutableLiveData<>();
        mResultTickerInfo = Transformations.switchMap(
                mSearchChanceInfo, input -> mUpBitFetcher.getTicker(input)
        );



/*        mResultChanceMarketInfo = Transformations.map(
                mResultChanceInfo,
                Chance::getMarketItems);

        mResultChanceBidInfo = Transformations.map(
                mResultChanceInfo,
                Chance::getBidItems);

        mResultChanceAskInfo = Transformations.map(
                mResultChanceInfo,
                Chance::getAskItems);*/
    }


    @Override
    protected void initFetcher(Context context) {
        mUpBitFetcher = new UpBitFetcher(null);
    }

    public LiveData<Chance> getResultChanceInfo() {
        return mResultChanceInfo;
    }

    public void searchChanceInfo(String markerId) {
        mSearchChanceInfo.setValue(markerId);
    }

    public LiveData<Ticker> getResultTickerInfo() {
        return mResultTickerInfo;
    }

    public void searchTickerInfo(String markerId) {
        mSearchTickerInfo.setValue(markerId);
    }


}
