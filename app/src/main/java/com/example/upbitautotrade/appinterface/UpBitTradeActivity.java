package com.example.upbitautotrade.appinterface;

import android.app.Activity;

import com.example.upbitautotrade.utils.BackgroundProcessor;
import com.example.upbitautotrade.viewmodel.AccountsViewModel;
import com.example.upbitautotrade.viewmodel.UpBitViewModel;

public interface UpBitTradeActivity {
    public Activity getActivity();
    public UpBitViewModel getViewModel();
    public AccountsViewModel getAccountsViewModel();
    public BackgroundProcessor getProcessor();
}
