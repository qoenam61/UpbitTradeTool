package com.example.upbitautotrade.appinterface;

import android.app.Activity;
import android.os.Handler;

import com.example.upbitautotrade.UpBitViewModel;
import com.example.upbitautotrade.model.Accounts;

import java.util.List;

public interface UpBitAutoTradeActivity {
    public Activity getActivity();
    public void setAccessKey(String accessKey);
    public void setSecretKey(String secretKey);
    public boolean isAuthorization();
    public UpBitViewModel getViewModel();
    public List<Accounts> getAccountInfo();
    public Handler getRequestHandler();
}
