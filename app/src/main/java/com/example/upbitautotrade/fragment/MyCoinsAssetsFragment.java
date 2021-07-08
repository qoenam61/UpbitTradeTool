package com.example.upbitautotrade.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.upbitautotrade.R;
import com.example.upbitautotrade.UpBitViewModel;
import com.example.upbitautotrade.appinterface.UpBitTradeActivity;
import com.example.upbitautotrade.model.Accounts;

import java.util.List;

public class MyCoinsAssetsFragment extends Fragment {
    private static final String TAG = "MyCoinsAssetsFragment";

    private final int PERIODIC_UPDATE_ACCOUNTS_INFO = 1;

    private UpBitTradeActivity mActivity;
    private List<Accounts> mAccountsInfo;
    private View mView;
    private UpBitViewModel mUpBitViewModel;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PERIODIC_UPDATE_ACCOUNTS_INFO:
                    mUpBitViewModel.searchAccountsInfo(false);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (UpBitTradeActivity)getActivity();
        mUpBitViewModel = mActivity.getViewModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_my_coins_assets, container, false);
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mActivity.getViewModel().getAccountsInfo().observe(
                getViewLifecycleOwner()
                , accounts -> {
                    mAccountsInfo = accounts;
                    updateAccountInfo();
                }
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        process();
    }

    private void process() {
        new Thread(() -> {
            try {
                long latency = 0;
                int i = 0;
                while (true) {
                    mHandler.sendEmptyMessage(PERIODIC_UPDATE_ACCOUNTS_INFO);
                    Thread.sleep(1000);
                    i = (i + 1) % 10;
                }
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateAccountInfo() {
        TextView currencyValue = mView.findViewById(R.id.asset_currency_value);
        TextView balanceValue = mView.findViewById(R.id.assets_balance_value);
        currencyValue.setText(mAccountsInfo.get(0).getCurrency());
        balanceValue.setText(mAccountsInfo.get(0).getBalance());
    }
}
