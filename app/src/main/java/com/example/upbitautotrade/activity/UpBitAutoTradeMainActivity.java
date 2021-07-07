package com.example.upbitautotrade.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.upbitautotrade.R;
import com.example.upbitautotrade.UpBitLogInPreferences;
import com.example.upbitautotrade.UpBitViewModel;
import com.example.upbitautotrade.appinterface.UpBitAutoTradeActivity;
import com.example.upbitautotrade.fragment.UpBitLoginFragment;
import com.example.upbitautotrade.model.Accounts;
import com.example.upbitautotrade.model.Market;

import java.util.List;
import java.util.UUID;

public class UpBitAutoTradeMainActivity extends AppCompatActivity implements UpBitAutoTradeActivity {
    private static final String TAG = "PhotoGalleryActivity";

    public static final int REQUEST_GET_LOGIN_INFO = 0;
    public static final int REQUEST_GET_ACCOUNTS_INFO = 1;

    private UpBitViewModel mUpBitViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upbit_auto_trade_main);

        boolean isFragmentContainer = savedInstanceState == null;
        UpBitLogInPreferences.create(this);

        Log.d(TAG, "onCreate: ");
        if (isFragmentContainer) {
            UpBitLoginFragment upbitLoginFragment = new UpBitLoginFragment();
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().add(R.id.fragmentContainer, upbitLoginFragment).commit();
        }
        mUpBitViewModel = new ViewModelProvider(this).get(UpBitViewModel.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
/*        mUpBitViewModel.getAccountsInfo().observe(
                this
                , accounts -> {
                    Log.d(TAG, "[DEBUG] main onStart: ");
                    mAccountsInfo = accounts;
                }
        );

        mUpBitViewModel.getResultChanceMarketInfo().observe(
                this, markets -> {
                    mMarketInfo = markets;
                }
        );

        mUpBitViewModel.getResultChanceBidInfo().observe(
                this, accounts -> {
                    mBidInfo = accounts;
                }
        );

        mUpBitViewModel.getResultChanceAskInfo().observe(
                this, accounts -> {
                    mAskInfo = accounts;
                }
        );*/
    }

    @Override
    public UpBitViewModel getViewModel() {
        return mUpBitViewModel;
    }
}