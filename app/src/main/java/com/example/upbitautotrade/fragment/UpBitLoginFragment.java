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

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.upbitautotrade.R;
import com.example.upbitautotrade.UpBitLogInPreferences;
import com.example.upbitautotrade.UpBitViewModel;
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


        EditText access = mView.findViewById(R.id.edit_access_key);
        EditText secret = mView.findViewById(R.id.edit_secret_key);

        String accessKey = UpBitLogInPreferences.getStoredKey(UpBitLogInPreferences.ACCESS_KEY);
        String secretKey = UpBitLogInPreferences.getStoredKey(UpBitLogInPreferences.SECRET_KEY);
        Log.d(TAG, "[DEBUG] onCreateView -accessKey: "+accessKey+" secretKey: "+secretKey);
        if (accessKey != null && secretKey != null) {
            access.setText(accessKey);
            secret.setText(secretKey);
        }
        mUpBitViewModel.setOnListener(new UpBitViewModel.LoginState() {
            @Override
            public void onLoginState(boolean isLogin) {
                Log.d(TAG, "[DEBUG] onLoginState: "+isLogin);
                if (isLogin) {
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    MyCoinsAssetsFragment myCoinsAssetsFragment = new MyCoinsAssetsFragment();
                    transaction.replace(R.id.fragmentContainer, myCoinsAssetsFragment);
                    transaction.commit();
                }
            }
        });
        return mView;
    }

    private void onResultButton() {
        TextView accessKey = mView.findViewById(R.id.result_access_key);
        TextView secretKey = mView.findViewById(R.id.result_secret_key);

        if (mAccountsInfo != null) {
            accessKey.setText(mAccountsInfo.get(0).getBalance());
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

        mUpBitViewModel.setKey(accessKey, secretKey);

        InputMethodManager mInputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(access.getWindowToken(), 0);

        mUpBitViewModel.searchAccountsInfo();

        Log.d(TAG, "[DEBUG] onLoginButton: "+isSuccessConnection());
    }

    private boolean isSuccessConnection() {
        return mUpBitViewModel.isSuccessfulConnection();
    }
}
