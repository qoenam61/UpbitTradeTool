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
import com.example.upbitautotrade.utils.BackgroundProcessor;
import com.example.upbitautotrade.viewmodel.AccountsViewModel;
import com.example.upbitautotrade.viewmodel.UpBitViewModel;
import com.example.upbitautotrade.appinterface.UpBitTradeActivity;
import com.example.upbitautotrade.model.Accounts;

import java.util.List;

import static com.example.upbitautotrade.utils.BackgroundProcessor.PERIODIC_UPDATE_ACCOUNTS_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.PERIODIC_UPDATE_CHANCE_INFO;

public class MyCoinsAssetsFragment extends Fragment {
    private static final String TAG = "MyCoinsAssetsFragment";

    private UpBitTradeActivity mActivity;
    private List<Accounts> mAccountsInfo;
    private View mView;
    private Chance mChanceInfo;
    private AccountsViewModel mViewModel;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (UpBitTradeActivity)getActivity();
        mViewModel =  mActivity.getAccountsViewModel();
        mActivity.getProcessor().setRegisterPeriodicUpdate(null, PERIODIC_UPDATE_ACCOUNTS_INFO);
        mActivity.getProcessor().setRegisterPeriodicUpdate("KRW-ETH", PERIODIC_UPDATE_CHANCE_INFO);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_my_coins_assets, container, false);
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mViewModel != null) {
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
    }

    @Override
    public void onPause() {
        super.onPause();
        mActivity.getProcessor().removePeriodicUpdate(PERIODIC_UPDATE_ACCOUNTS_INFO);
        mActivity.getProcessor().removePeriodicUpdate(PERIODIC_UPDATE_CHANCE_INFO);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void updateAccountInfo() {
        TextView currencyValue = mView.findViewById(R.id.asset_currency_value);
        TextView balanceValue = mView.findViewById(R.id.assets_balance_value);
        currencyValue.setText(mAccountsInfo.get(0).getCurrency());
        balanceValue.setText(mAccountsInfo.get(0).getBalance());
    }
}
