package com.example.upbitautotrade.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.example.upbitautotrade.R;
import com.example.upbitautotrade.UpBitLogInPreferences;
import com.example.upbitautotrade.utils.BackgroundProcessor;
import com.example.upbitautotrade.viewmodel.AccountsViewModel;
import com.example.upbitautotrade.viewmodel.UpBitViewModel;
import com.example.upbitautotrade.appinterface.UpBitTradeActivity;
import com.example.upbitautotrade.fragment.UpBitLoginFragment;

public class UpBitTradeMainActivity extends AppCompatActivity implements UpBitTradeActivity {
    private static final String TAG = "UpBitTradeMainActivity";

    public static final int REQUEST_GET_LOGIN_INFO = 0;
    public static final int REQUEST_GET_ACCOUNTS_INFO = 1;

    private UpBitViewModel mUpBitViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upbit_auto_trade_main);

        boolean isFragmentContainer = savedInstanceState == null;
        UpBitLogInPreferences.create(this);

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
    }

    @Override
    public Activity getActivity() {
        return getActivity();
    }

    @Override
    public UpBitViewModel getViewModel() {
        return mUpBitViewModel;
    }

    @Override
    public AccountsViewModel getAccountsViewModel() {
        return null;
    }

    @Override
    public BackgroundProcessor getProcessor() {
        return null;
    }
}