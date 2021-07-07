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

    public static final int REQUEST_GET_ACCOUNTS_INFO = 0;

    private UpBitViewModel mUpBitViewModel;
    private String mAccessKey;
    private String mSecretKey;

    private List<Accounts> mAccountsInfo;
    private List<Market> mMarketInfo;
    private List<Accounts> mBidInfo;
    private List<Accounts> mAskInfo;

    private HandlerThread mHandlerThread = new HandlerThread("background");
    private Handler mHandler = new Handler(mHandlerThread.getLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REQUEST_GET_ACCOUNTS_INFO:
                    mUpBitViewModel.searchAccountsInfo();
                    break;
                default:
                    break;
            }
        }
    };

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
        mUpBitViewModel.getAccountsInfo().observe(
                this
                , accounts -> {
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
        );
    }

    @Override
    public Activity getActivity() {
        return null;
    }

    @Override
    public void setAccessKey(String accessKey) {
        mAccessKey = accessKey;
    }

    @Override
    public void setSecretKey(String secretKey) {
        mSecretKey = secretKey;
    }

    @Override
    public boolean isAuthorization() {
//        UpBitLogInPreferences.setStoredKey(getContext(), "access_key", accessKey.getText().toString());
//        UpBitLogInPreferences.setStoredKey(getContext(), "secret_key", secretKey.getText().toString());
        return false;
    }

    @Override
    public UpBitViewModel getViewModel() {
        return mUpBitViewModel;
    }

    @Override
    public List<Accounts> getAccountInfo() {
        return mAccountsInfo;
    }

    @Override
    public Handler getRequestHandler() {
        return mHandler;
    }
}