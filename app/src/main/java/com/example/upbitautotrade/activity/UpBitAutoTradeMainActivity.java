package com.example.upbitautotrade.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;

import com.example.upbitautotrade.R;
import com.example.upbitautotrade.UpBitLogInPreferences;
import com.example.upbitautotrade.UpBitViewModel;
import com.example.upbitautotrade.fragment.UpBitLoginFragment;

public class UpBitAutoTradeMainActivity extends AppCompatActivity {
    private static final String TAG = "PhotoGalleryActivity";
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
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}