package com.example.upbitautotrade.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.upbitautotrade.R;
import com.example.upbitautotrade.appinterface.UpBitAutoTradeActivity;

public class MyCoinsAssetsFragment extends Fragment {

    private UpBitAutoTradeActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mActivity = (UpBitAutoTradeActivity)getActivity();
        View view = inflater.inflate(R.layout.fragment_my_coins_assets, container, false);
        TextView currencyValue = view.findViewById(R.id.asset_currency_value);
        TextView balanceValue = view.findViewById(R.id.assets_balance_value);

        currencyValue.setText(mActivity.getAccountInfo().get(0).getCurrency());
        balanceValue.setText(mActivity.getAccountInfo().get(0).getBalance());

        mActivity.getRequestHandler().sen
        return view;
    }
}
