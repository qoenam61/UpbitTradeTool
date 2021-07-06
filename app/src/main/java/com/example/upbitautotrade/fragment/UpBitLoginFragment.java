package com.example.upbitautotrade.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.upbitautotrade.R;
import com.example.upbitautotrade.UpBitLogInPreferences;
import com.example.upbitautotrade.UpBitViewModel;
import com.example.upbitautotrade.activity.UpBitAutoTradeMainActivity;
import com.example.upbitautotrade.appinterface.UpBitAutoTradeActivity;
import com.example.upbitautotrade.model.Accounts;
import com.example.upbitautotrade.model.Market;

import java.util.List;

public class UpBitLoginFragment extends Fragment {
    private final String TAG = "UpBitLoginFragment";

    private UpBitAutoTradeActivity mActivity;
    private View mView;
    private UpBitViewModel mUpBitViewModel;
    private List<Accounts> mAccountsInfo;
    private List<Market> mMarketInfo;
    private List<Accounts> mBidInfo;
    private List<Accounts> mAskInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (UpBitAutoTradeActivity)getActivity();
        mUpBitViewModel = new ViewModelProvider(this).get(UpBitViewModel.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        mUpBitViewModel.getAccountsInfo().observe(
                getViewLifecycleOwner(), accounts -> {
                    mAccountsInfo = accounts;
                }
        );

        mUpBitViewModel.getResultChanceMarketInfo().observe(
                getViewLifecycleOwner(), markets -> {
                    mMarketInfo = markets;
                }
        );

        mUpBitViewModel.getResultChanceBidInfo().observe(
                getViewLifecycleOwner(), accounts -> {
                    mBidInfo = accounts;
                }
        );

        mUpBitViewModel.getResultChanceAskInfo().observe(
                getViewLifecycleOwner(), accounts -> {
                    mAskInfo = accounts;
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_login_main, container, false);

        Button loginButton = mView.findViewById(R.id.btn_login);
        loginButton.setOnClickListener(v -> onLoginButton());

        Button resultButton = mView.findViewById(R.id.btn_result);
        resultButton.setOnClickListener(v -> onResultButton());

        return mView;
    }

    private void onResultButton() {
        Log.d(TAG, "[DEBUG] onResultButton: ");
        TextView accessKey = mView.findViewById(R.id.result_access_key);
        TextView secretKey = mView.findViewById(R.id.result_secret_key);

        if (mAccountsInfo != null) {
//            accessKey.setText(mAccountsInfo.getBalance());
            secretKey.setText(mAccountsInfo.get(0).getCurrency());
        } else {
            Log.d(TAG, "[DEBUG] onResultButton: null");
        }
    }

    private void onLoginButton() {
        EditText access = mView.findViewById(R.id.edit_access_key);
        EditText secret = mView.findViewById(R.id.edit_secret_key);
        String accessKey = access.getText().toString();
        String secretKey = secret.getText().toString();

        mActivity.setAccessKey(accessKey);
        mActivity.setSecretKey(secretKey);
        Log.d(TAG, "[DEBUG] onLoginButton +accessKey: "+accessKey+" secretKey: "+secretKey);
        mUpBitViewModel.setKey(accessKey, secretKey);

        InputMethodManager mInputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(access.getWindowToken(), 0);

        mUpBitViewModel.searchAccountsInfo();
//        Log.d(TAG, "[DEBUG] onLoginButton - getAccountsInfo: "+ mAccountsInfo != null ? mAccountsInfo.toString() : null);
//        if (isSuccessConnection()) {
//            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//            MyCoinsAssetsFragment myCoinsAssetsFragment = new MyCoinsAssetsFragment();
//            transaction.replace(R.id.fragmentContainer, myCoinsAssetsFragment);
//            transaction.commit();
//        }
    }

    private boolean isSuccessConnection() {
        return UpBitLogInPreferences.getStoredKey(UpBitLogInPreferences.ACCESS_KEY) != null
                && UpBitLogInPreferences.getStoredKey(UpBitLogInPreferences.SECRET_KEY) != null;
    }
}
