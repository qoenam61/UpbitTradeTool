package com.example.upbitautotrade.appinterface;

import android.app.Activity;

public interface UpBitAutoTradeActivity {
    public Activity getActivity();
    public void setAccessKey(String accessKey);
    public void setSecretKey(String secretKey);
    public boolean isAuthorization();
}
