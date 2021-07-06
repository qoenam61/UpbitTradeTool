package com.example.upbitautotrade.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.upbitautotrade.R;

public class MyCoinsAssetsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_coins_assets, container, false);
        TextView currencyValue = view.findViewById(R.id.asset_currency_value);
        TextView balanceValue = view.findViewById(R.id.assets_balance_value);
        return view;
    }
}
