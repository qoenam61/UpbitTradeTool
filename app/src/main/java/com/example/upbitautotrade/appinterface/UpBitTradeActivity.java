package com.example.upbitautotrade.appinterface;

import android.app.Activity;
import android.os.Handler;

import com.example.upbitautotrade.UpBitViewModel;
import com.example.upbitautotrade.model.Accounts;

import java.util.List;

public interface UpBitTradeActivity {
    public Activity getActivity();
    public UpBitViewModel getViewModel();
}
