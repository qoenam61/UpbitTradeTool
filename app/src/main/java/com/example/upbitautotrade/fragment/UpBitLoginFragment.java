package com.example.upbitautotrade.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.upbitautotrade.R;
import com.example.upbitautotrade.UpBitLogInPreferences;
import com.example.upbitautotrade.viewmodel.UpBitViewModel;
import com.example.upbitautotrade.activity.TradePagerActivity;
import com.example.upbitautotrade.appinterface.UpBitTradeActivity;

public class UpBitLoginFragment extends Fragment {
    private final String TAG = "UpBitLoginFragment";

    private UpBitTradeActivity mActivity;
    private View mView;
    private UpBitViewModel mUpBitViewModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (UpBitTradeActivity)getActivity();
        mUpBitViewModel = mActivity.getViewModel();
    }

    @Override
    public void onStart() {
        super.onStart();
        mUpBitViewModel.getAccountsInfo().observe(
                getViewLifecycleOwner()
                , accounts -> {
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_login_main, container, false);

        Button loginButton = mView.findViewById(R.id.btn_login);
        loginButton.setOnClickListener(v -> onLoginButton());

        EditText access = mView.findViewById(R.id.edit_access_key);
        EditText secret = mView.findViewById(R.id.edit_secret_key);
        String accessKey = UpBitLogInPreferences.getStoredKey(UpBitLogInPreferences.ACCESS_KEY);
        String secretKey = UpBitLogInPreferences.getStoredKey(UpBitLogInPreferences.SECRET_KEY);

        if (accessKey != null && secretKey != null) {
            access.setText(accessKey);
            secret.setText(secretKey);
        }
        mUpBitViewModel.setOnListener(new UpBitViewModel.LoginState() {
            @Override
            public void onLoginState(boolean isLogin) {
                Log.d(TAG, "onLoginState: "+isLogin);
/*
                if (isLogin) {
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    MyCoinsAssetsFragment myCoinsAssetsFragment = new MyCoinsAssetsFragment();
                    transaction.replace(R.id.fragmentContainer, myCoinsAssetsFragment);
                    transaction.commit();
                }
*/
                if (isLogin) {
                    Intent intent = new Intent(getActivity(), TradePagerActivity.class);
                    startActivity(intent);
                }
            }
        });
        return mView;
    }

    private void onLoginButton() {
        EditText access = mView.findViewById(R.id.edit_access_key);
        EditText secret = mView.findViewById(R.id.edit_secret_key);
        String accessKey = access.getText().toString();
        String secretKey = secret.getText().toString();
        mUpBitViewModel.setKey(accessKey, secretKey);
        mUpBitViewModel.searchAccountsInfo(true);

        InputMethodManager mInputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(access.getWindowToken(), 0);
    }
}
