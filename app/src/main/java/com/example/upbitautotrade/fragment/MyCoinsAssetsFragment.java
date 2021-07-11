package com.example.upbitautotrade.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upbitautotrade.R;
import com.example.upbitautotrade.model.Chance;
import com.example.upbitautotrade.model.Ticker;
import com.example.upbitautotrade.viewmodel.AccountsViewModel;
import com.example.upbitautotrade.appinterface.UpBitTradeActivity;
import com.example.upbitautotrade.model.Accounts;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.example.upbitautotrade.utils.BackgroundProcessor.PERIODIC_UPDATE_ACCOUNTS_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.PERIODIC_UPDATE_CHANCE_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.PERIODIC_UPDATE_TICKER_INFO;

public class MyCoinsAssetsFragment extends Fragment {
    private static final String TAG = "MyCoinsAssetsFragment";

    private final String MARKET_NAME = "KRW";

    private View mView;
    private UpBitTradeActivity mActivity;
    private HashMap<String, Accounts> mAccountsMapInfo;
    private Chance mChanceInfo;
    private HashMap<String, Ticker> mTickerMapInfo;
    private final HashMap<String, Ticker> mTickerTempMapInfo;
    private Set<String> mKeySet;
    private CoinListAdapter mCoinListAdapter;

    private AccountsViewModel mViewModel;

    public MyCoinsAssetsFragment() {
        mAccountsMapInfo = new HashMap<>();
        mTickerMapInfo = new HashMap<>();
        mTickerTempMapInfo = new HashMap<>();
        mKeySet = new HashSet<>();
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (UpBitTradeActivity)getActivity();
        mViewModel =  mActivity.getAccountsViewModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_my_coins_assets, container, false);
        RecyclerView coinList = mView.findViewById(R.id.coin_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        coinList.setLayoutManager(layoutManager);
        mCoinListAdapter = new CoinListAdapter();
        coinList.setAdapter(mCoinListAdapter);
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mViewModel != null) {
            mViewModel.getAccountsInfo().observe(
                    getViewLifecycleOwner()
                    , accounts -> {
                        mAccountsMapInfo.clear();
                        Iterator<Accounts> iterator = accounts.iterator();
                        while (iterator.hasNext()) {
                            Accounts account = iterator.next();
                            mAccountsMapInfo.put(account.getCurrency(), account);
                            if (account.getCurrency().equals("KRW")) {
                                iterator.remove();
                            }
                        }
                        updateKeySets(mAccountsMapInfo.keySet());
                        updateAccountInfo();
                        mCoinListAdapter.setItems(accounts);
                        mCoinListAdapter.notifyDataSetChanged();
                    }
            );

            mViewModel.getResultChanceInfo().observe(
                    getViewLifecycleOwner(),
                    chance -> {
                        mChanceInfo = chance;
                    }
            );

            mViewModel.getResultTickerInfo().observe(
                    getViewLifecycleOwner(),
                    ticker -> {
                        Iterator<Ticker> iterator = ticker.iterator();
                        while (iterator.hasNext()) {
                            Ticker tick = iterator.next();
                            mTickerMapInfo.put(tick.getMarket(), tick);
                        }
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
        mActivity.getProcessor().registerPeriodicUpdate(null, PERIODIC_UPDATE_ACCOUNTS_INFO);
    }

    private void updateKeySets(Set<String> keySet) {
        boolean update = false;
        if (!keySet.equals(mKeySet)) {
            removePeriodicUpdate(mKeySet);
            mKeySet.clear();
            mKeySet = keySet;
            registerPeriodicUpdate(mKeySet);
            update = true;
        }
        if (update) {
            Iterator<String> iterator = mKeySet.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (!keySet.contains(key)) {
                    mTickerMapInfo.remove(key);
                }
            }
        }
    }

    private void removePeriodicUpdate(Set<String> keySet) {
        Iterator<String> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            mActivity.getProcessor().removePeriodicUpdate(MARKET_NAME + "-" + key);
            mActivity.getProcessor().removePeriodicUpdate(MARKET_NAME + "-" + key);
        }
    }

    private void registerPeriodicUpdate(Set<String> keySet) {
        Iterator<String> regIterator = keySet.iterator();
        while (regIterator.hasNext()) {
            String key = regIterator.next();
            mActivity.getProcessor().registerPeriodicUpdate(MARKET_NAME + "-" + key, PERIODIC_UPDATE_ACCOUNTS_INFO);
            if (!key.equals("KRW-KRW")) {
                mActivity.getProcessor().registerPeriodicUpdate(MARKET_NAME + "-" + key, PERIODIC_UPDATE_TICKER_INFO);
            }
        }
    }

    private void updateAccountInfo() {
        DecimalFormat format = new DecimalFormat("###,###,###,###");
        format.setDecimalSeparatorAlwaysShown(true);
        TextView balanceValue = mView.findViewById(R.id.assets_balance_value);
        balanceValue.setText(format.format(getCurrentBalance()));

        DecimalFormat formatNonZero = new DecimalFormat("###,###,###");
        formatNonZero.setDecimalSeparatorAlwaysShown(true);
        TextView totalAmountValue = mView.findViewById(R.id.assets_total_amount_value);
        totalAmountValue.setText(formatNonZero.format(getTotalAmount()));
    }

    private int getCurrentBalance() {
        int balance = 0;
        balance += mAccountsMapInfo.get(MARKET_NAME).getBalance().intValue();
        balance += mAccountsMapInfo.get(MARKET_NAME).getLocked().intValue();
        return balance;
    }

    private float getTotalAmount() {
        float balance = 0;
        if (mTickerMapInfo.isEmpty()) {
            return -1;
        }

        Iterator<String> iterator = mKeySet.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (key.equals(MARKET_NAME)) {
                balance += mAccountsMapInfo.get(key).getBalance().floatValue() + mAccountsMapInfo.get(key).getLocked().floatValue();
                continue;
            }
            Ticker ticker = mTickerMapInfo.get(MARKET_NAME+"-"+key);
            if (ticker == null) {
                continue;
            }
            float price = ticker.getTradePrice().floatValue();
            balance += mAccountsMapInfo.get(key).getBalance().floatValue() * price;
            balance += mAccountsMapInfo.get(key).getLocked().floatValue() * price;
        }
        return balance;
    }

    private class CoinHolder extends RecyclerView.ViewHolder {
        private final View mCoinInfoView;

        public TextView mCoinName;
        public TextView mCoinCurrency;
        public TextView mProfit;
        public TextView mProfitRate;
        public TextView mBalance;
        public TextView mPrice;
        public TextView mAvgPrice;
        public TextView mAmount;

        public CoinHolder(View itemView) {
            super(itemView);
            mCoinInfoView = itemView;
            mCoinName = itemView.findViewById(R.id.coin_name);
            mCoinCurrency = itemView.findViewById(R.id.coin_currency);
            mProfit = itemView.findViewById(R.id.coin_profit);
            mProfitRate = itemView.findViewById(R.id.coin_profit_rate);
            mBalance = itemView.findViewById(R.id.coin_balance);
            mPrice = itemView.findViewById(R.id.coin_price);
            mAvgPrice = itemView.findViewById(R.id.coin_avg_price);
            mAmount = itemView.findViewById(R.id.coin_total_price);
        }
    }

    private class CoinListAdapter extends RecyclerView.Adapter<CoinHolder> {
        private List<Accounts> mCoinAccounts;
        DecimalFormat mFormat;

        public void setItems(List<Accounts> accounts) {
            mCoinAccounts = accounts;
            mFormat = new DecimalFormat("###,###,###,###.###");
            mFormat.setDecimalSeparatorAlwaysShown(true);
        }

        @Override
        public CoinHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.accounts_coin_item, parent, false);
            return new CoinHolder(view);
        }

        @Override
        public void onBindViewHolder(CoinHolder holder, int position) {
            holder.mCoinName.setText(mCoinAccounts.get(position).getCurrency());
            holder.mCoinCurrency.setText(mCoinAccounts.get(position).getCurrency());
            holder.mBalance.setText(mFormat.format(mCoinAccounts.get(position).getTotalBalance()));
            holder.mPrice.setText(mFormat.format(mCoinAccounts.get(position).getAvgBuyPrice()));
        }

        @Override
        public int getItemCount() {
            return mCoinAccounts != null ? mCoinAccounts.size() : 0;
        }
    }
}
