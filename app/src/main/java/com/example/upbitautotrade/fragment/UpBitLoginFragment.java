package com.example.upbitautotrade.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.upbitautotrade.R;
import com.example.upbitautotrade.UpBitLogInPreferences;

public class UpBitLoginFragment extends Fragment {
    private final String TAG = "UpBitLoginFragment";
    private View mView;
    private Button mButton;
    private Button mResultButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_login_main, container, false);

        UpBitLogInPreferences.create(getContext());

        mButton = mView.findViewById(R.id.btn_login);
        mButton.setOnClickListener(v -> onLoginButton());

        mResultButton = mView.findViewById(R.id.btn_result);
        mResultButton.setOnClickListener(v -> onResultButton());
        return mView;
    }

    private void onResultButton() {
        TextView accessKey = mView.findViewById(R.id.text_result1);
        TextView secretKey = mView.findViewById(R.id.text_result2);
        Log.d(TAG, "[DEBUG] onResultButton: "+UpBitLogInPreferences.getStoredKey(getContext(), "access_key"));

        accessKey.setText(UpBitLogInPreferences.getStoredKey(getContext(), "access_key"));
        secretKey.setText(UpBitLogInPreferences.getStoredKey(getContext(), "secret_key"));
    }

    private void onLoginButton() {
        Log.d(TAG, "[DEBUG] onLoginButton: ");
        EditText accessKey = mView.findViewById(R.id.edit_access_key);
        EditText secretKey = mView.findViewById(R.id.edit_secret_key);
        UpBitLogInPreferences.setStoredKey(getContext(), "access_key", accessKey.getText().toString());
        UpBitLogInPreferences.setStoredKey(getContext(), "secret_key", secretKey.getText().toString());
    }


}
