package com.example.upbitautotrade.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.upbitautotrade.R;
import com.example.upbitautotrade.UpBitLogInPreferences;
import com.example.upbitautotrade.model.Chance;
import com.example.upbitautotrade.viewmodel.AccountsViewModel;
import com.example.upbitautotrade.viewmodel.UpBitViewModel;
import com.example.upbitautotrade.appinterface.UpBitTradeActivity;
import com.example.upbitautotrade.model.Accounts;

import java.util.List;

public class MyCoinsAssetsFragment extends Fragment {
    private static final String TAG = "MyCoinsAssetsFragment";

    private final int PERIODIC_UPDATE_ACCOUNTS_INFO = 1;

    private UpBitTradeActivity mActivity;
    private List<Accounts> mAccountsInfo;
    private View mView;
    private AccountsViewModel mViewModel;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PERIODIC_UPDATE_ACCOUNTS_INFO:
                    mViewModel.searchAccountsInfo(false);
                    break;
                default:
                    break;
            }
        }
    };
    private Thread mProcess;
    private Chance mChanceInfo;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AccountsViewModel.class);
        mViewModel.setKey(UpBitLogInPreferences.getStoredKey(UpBitLogInPreferences.ACCESS_KEY),
                UpBitLogInPreferences.getStoredKey(UpBitLogInPreferences.SECRET_KEY));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_my_coins_assets, container, false);
        Button button = mView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.searchChanceInfo("KRW-BTC");
            }
        });
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "[DEBUG] onStart: ");
        mViewModel.getAccountsInfo().observe(
                getViewLifecycleOwner()
                , accounts -> {
                    mAccountsInfo = accounts;
                    updateAccountInfo();
                }
        );

        mViewModel.getResultChanceInfo().observe(
                getViewLifecycleOwner(),
                chance -> {
                    mChanceInfo = chance;
                }
        );

        mViewModel.getErrorLiveData()
                .observe(
                        getViewLifecycleOwner(),
                        t -> {
                            Toast.makeText(getContext(),
                                    t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                );

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "[DEBUG] onPause: ");

        Thread process = mProcess;
        if (process != null) {
            mProcess.interrupt();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "[DEBUG] onResume: ");
        process();
    }

    private void process() {
        mProcess = new Thread(() -> {
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
        });
        mProcess.start();
    }

    private void updateAccountInfo() {
        TextView currencyValue = mView.findViewById(R.id.asset_currency_value);
        TextView balanceValue = mView.findViewById(R.id.assets_balance_value);
        currencyValue.setText(mAccountsInfo.get(0).getCurrency());
        balanceValue.setText(mAccountsInfo.get(0).getBalance());
    }
}
