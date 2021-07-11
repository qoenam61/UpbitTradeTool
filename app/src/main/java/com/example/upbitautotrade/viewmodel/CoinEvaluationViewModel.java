package com.example.upbitautotrade.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.upbitautotrade.model.Ticker;

import java.util.List;

public class CoinEvaluationViewModel extends UpBitViewModel{
    private final String TAG = "CoinEvaluationViewModel";

    private final MutableLiveData<String> mSearchTickerInfo;
    private final LiveData<List<Ticker>> mResultTickerInfo;

    public CoinEvaluationViewModel(Application application) {
        super(application);

        mSearchTickerInfo = new MutableLiveData<>();
        mResultTickerInfo = Transformations.switchMap(
                mSearchTickerInfo, input -> mUpBitFetcher.getTicker(input)
        );
    }

    public LiveData<List<Ticker>> getResultTickerInfo() {
        return mResultTickerInfo;
    }

    public void searchTickerInfo(String markerId) {
        mSearchTickerInfo.setValue(markerId);
    }
}
