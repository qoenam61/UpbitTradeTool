package com.example.upbitautotrade.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.upbitautotrade.R;
import com.example.upbitautotrade.UpBitLogInPreferences;
import com.example.upbitautotrade.UpBitViewModel;
import com.example.upbitautotrade.appinterface.UpBitAutoTradeActivity;
import com.example.upbitautotrade.fragment.UpBitLoginFragment;

import java.util.UUID;

public class UpBitAutoTradeMainActivity extends AppCompatActivity implements UpBitAutoTradeActivity {
    private static final String TAG = "PhotoGalleryActivity";

    private UpBitViewModel mUpBitViewModel;
    private String mAccessKey;
    private String mSecretKey;

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
    }

    @Override
    protected void onStart() {
        super.onStart();

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
}