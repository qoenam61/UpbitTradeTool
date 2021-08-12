package com.example.upbitautotrade.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upbitautotrade.R;
import com.example.upbitautotrade.model.MarketInfo;
import com.example.upbitautotrade.model.Ticker;
import com.example.upbitautotrade.viewmodel.AccountsViewModel;
import com.example.upbitautotrade.appinterface.UpBitTradeActivity;
import com.example.upbitautotrade.model.Accounts;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_ACCOUNTS_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_CHANCE_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_MARKETS_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_MIN_CANDLE_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_TICKER_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_TRADE_INFO;

public class MyCoinsAssetsFragment extends Fragment {
    private static final String TAG = "MyCoinsAssetsFragment";

    private final String MARKET_NAME = "KRW";

    private View mView;
    private UpBitTradeActivity mActivity;
    private HashMap<String, MarketInfo> mMarketsMapInfo;
    private HashMap<String, Accounts> mAccountsMapInfo;
    private HashMap<String, Ticker> mTickerMapInfo;
    private Set<String> mKeySet;
    private CoinListAdapter mCoinListAdapter;

    private AccountsViewModel mViewModel;

    boolean mIsActive = false;

    public MyCoinsAssetsFragment() {
        mMarketsMapInfo = new HashMap<>();
        mAccountsMapInfo = new HashMap<>();
        mTickerMapInfo = new HashMap<>();
        mKeySet = new HashSet<>();
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (UpBitTradeActivity)getActivity();
        mViewModel =  mActivity.getAccountsViewModel();

        if (savedInstanceState != null) {
            mMarketsMapInfo = (HashMap<String, MarketInfo>) savedInstanceState.getSerializable("marketMapInfo");
            mAccountsMapInfo = (HashMap<String, Accounts>) savedInstanceState.getSerializable("accountsMapInfo");
            mTickerMapInfo = (HashMap<String, Ticker>) savedInstanceState.getSerializable("tickerMapInfo");
            mKeySet = new HashSet<String>(savedInstanceState.getStringArrayList("keySet"));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        outState.putSerializable("marketMapInfo", mMarketsMapInfo);
        outState.putSerializable("accountsMapInfo", mAccountsMapInfo);
        outState.putSerializable("tickerMapInfo", mTickerMapInfo);
        outState.putStringArrayList("keySet", new ArrayList<String>(mKeySet));
        super.onSaveInstanceState(outState);
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
            mViewModel.getMarketsInfo().observe(
                    getViewLifecycleOwner(),
                    marketsInfo -> {
                        if (!mIsActive) {
                            return;
                        }
                        Iterator<MarketInfo> iterator = marketsInfo.iterator();
                        while (iterator.hasNext()) {
                            MarketInfo marketInfo = iterator.next();
                            mMarketsMapInfo.put(marketInfo.getMarketId(), marketInfo);
                        }
                    }
            );

            mViewModel.getAccountsInfo().observe(
                    getViewLifecycleOwner()
                    , accounts -> {
                        if (!mIsActive) {
                            return;
                        }
                        mAccountsMapInfo.clear();
                        Iterator<Accounts> iterator = accounts.iterator();
                        while (iterator.hasNext()) {
                            Accounts account = iterator.next();
                            Log.d(TAG, "[DEBUG] onStart put : "+account.getCurrency());
                            mAccountsMapInfo.put(account.getCurrency(), account);
                            if (account.getCurrency().equals("KRW")) {
                                iterator.remove();
                            }
                        }
                        Log.d(TAG, "[DEBUG] onStart: updateAccountInfo -size: "+mAccountsMapInfo.keySet().size());
                        updateKeySets(mAccountsMapInfo.keySet());
                        updateAccountInfo();
                        mCoinListAdapter.setItems(accounts);
                        mCoinListAdapter.notifyDataSetChanged();
                    }
            );

            mViewModel.getResultTickerInfo().observe(
                    getViewLifecycleOwner(),
                    ticker -> {
                        if (!mIsActive) {
                            return;
                        }
                        Iterator<Ticker> iterator = ticker.iterator();
                        while (iterator.hasNext()) {
                            Ticker tick = iterator.next();
                            mTickerMapInfo.put(tick.getMarketId(), tick);
                        }
                        Log.d(TAG, "[DEBUG] onStart: notifyDataSetChanged");
                        mCoinListAdapter.notifyDataSetChanged();
                    }
            );

            mViewModel.getErrorLiveData()
                    .observe(
                            getViewLifecycleOwner(),
                            t -> {
                                if (!mIsActive) {
                                    return;
                                }
                                Toast.makeText(getContext(),
                                        t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                    );
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "[DEBUG] onPause: ");
        mIsActive = false;
        mActivity.getProcessor().stopBackgroundProcessor();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "[DEBUG] onResume: ");
        mIsActive = true;
        mActivity.getProcessor().startBackgroundProcessor();
        mActivity.getProcessor().registerProcess(UPDATE_MARKETS_INFO, null);
        mActivity.getProcessor().registerPeriodicUpdate(UPDATE_ACCOUNTS_INFO, null);
    }

    private void updateKeySets(Set<String> keySet) {
        boolean update = false;
        if (!keySet.equals(mKeySet)) {
            removePeriodicUpdate(mKeySet);
            mKeySet.clear();
            mKeySet = keySet;
            update = true;
        }
        registerPeriodicUpdate(mKeySet);
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
            mActivity.getProcessor().removePeriodicUpdate(UPDATE_ACCOUNTS_INFO, MARKET_NAME + "-" + key);
            mActivity.getProcessor().removePeriodicUpdate(UPDATE_TICKER_INFO, MARKET_NAME + "-" + key);
        }
    }

    private void registerPeriodicUpdate(Set<String> keySet) {
        Iterator<String> regIterator = keySet.iterator();
        while (regIterator.hasNext()) {
            String key = regIterator.next();
            mActivity.getProcessor().registerPeriodicUpdate(UPDATE_ACCOUNTS_INFO, MARKET_NAME + "-" + key);
            if (!key.equals("KRW-KRW")) {
                mActivity.getProcessor().registerPeriodicUpdate(UPDATE_TICKER_INFO, MARKET_NAME + "-" + key);
            }
        }
    }

    private void updateAccountInfo() {
        DecimalFormat formatNonZero = new DecimalFormat("###,###,###");
        formatNonZero.setDecimalSeparatorAlwaysShown(true);
        TextView balanceValue = mView.findViewById(R.id.assets_balance_value);
        balanceValue.setText(formatNonZero.format(getTotalCurrentBalance()));
        TextView totalAmountValue = mView.findViewById(R.id.assets_total_amount_value);
        totalAmountValue.setText(formatNonZero.format(getTotalAmount()));
    }

    private int getTotalCurrentBalance() {
        if (mAccountsMapInfo == null) {
            return 0;
        }
        Accounts accounts = mAccountsMapInfo.get(MARKET_NAME);
        if (accounts == null) {
            return 0;
        }
        int balance = 0;
        balance += accounts.getBalance().intValue();
        balance += accounts.getLocked().intValue();
        return balance;
    }

    private int getTotalAmount() {
        float balance = 0;
        if (mTickerMapInfo.isEmpty()) {
            return 0;
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
        return (int) balance;
    }

    private class CoinHolder extends RecyclerView.ViewHolder {
        public TextView mCoinName;
        public TextView mCoinCurrency;
        public TextView mProfitAmount;
        public TextView mProfitRate;
        public TextView mBalance;
        public TextView mCurrentAmount;
        public TextView mAvgPrice;
        public TextView mBuyAmount;

        public CoinHolder(View itemView) {
            super(itemView);
            mCoinName = itemView.findViewById(R.id.coin_name);
            mCoinCurrency = itemView.findViewById(R.id.coin_currency);
            mProfitAmount = itemView.findViewById(R.id.coin_profit);
            mProfitRate = itemView.findViewById(R.id.coin_profit_rate);
            mBalance = itemView.findViewById(R.id.coin_balance);
            mCurrentAmount = itemView.findViewById(R.id.coin_current_value);
            mAvgPrice = itemView.findViewById(R.id.coin_avg_price);
            mBuyAmount = itemView.findViewById(R.id.coin_total_price);
        }
    }

    private class CoinListAdapter extends RecyclerView.Adapter<CoinHolder> {
        private List<Accounts> mCoinAccounts;
        DecimalFormat mFormat;
        DecimalFormat mNonZeroFormat;
        DecimalFormat mPercentFormat;

        public void setItems(List<Accounts> accounts) {
            mCoinAccounts = accounts;
            mFormat = new DecimalFormat("###,###,###,###.###");
            mNonZeroFormat = new DecimalFormat("###,###,###,###");
            mPercentFormat = new DecimalFormat("###.##" + "%");
        }

        @Override
        public CoinHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.accounts_coin_item, parent, false);
            return new CoinHolder(view);
        }

        @Override
        public void onBindViewHolder(CoinHolder holder, int position) {
            Accounts account = mCoinAccounts.get(position);
            holder.mCoinName.setText(getCoinName(account));
            holder.mBalance.setText(mFormat.format(account.getTotalBalance()));
            holder.mCoinCurrency.setText(account.getCurrency());
            holder.mCurrentAmount.setText(mNonZeroFormat.format(getCurrentAmount(account)));
            holder.mProfitAmount.setText(mNonZeroFormat.format(getProfitAmount(account)));
            holder.mProfitRate.setText(mPercentFormat.format(getProfitRate(account)));
            holder.mAvgPrice.setText(mNonZeroFormat.format(account.getAvgBuyPrice()));
            holder.mBuyAmount.setText(mNonZeroFormat.format(getBuyAmount(account)));
        }

        @Override
        public int getItemCount() {
            return mCoinAccounts != null ? mCoinAccounts.size() : 0;
        }

        private String getCoinName(Accounts account) {
            if (account == null) {
                return null;
            }
            MarketInfo marketInfo = mMarketsMapInfo.get(MARKET_NAME + "-" + account.getCurrency());
            return marketInfo != null ? marketInfo.getKorean_name() : null;
        }

        private float getBuyBalance(Accounts account) {
            if (account == null) {
                return 0;
            }
            return (account.getLocked().floatValue() + account.getBalance().floatValue());
        }

        private float getCurrentAmount(Accounts account) {
            if (account == null) {
                return 0;
            }
            return getBuyBalance(account) * getCurrentPrice(account);
        }

        private float getBuyAmount(Accounts account) {
            if (account == null) {
                return 0;
            }
            return getBuyBalance(account) * account.getAvgBuyPrice().floatValue();
        }

        private int getProfitAmount(Accounts account) {
            return (int) (getCurrentAmount(account) - getBuyAmount(account));
        }

        private float getProfitRate(Accounts account) {
            return getProfitAmount(account) / getBuyAmount(account);
        }

        private float getCurrentPrice(Accounts account) {
            Ticker ticker = mTickerMapInfo.get(MARKET_NAME+"-"+account.getCurrency());
            if (ticker == null) {
                return 0;
            }
            return ticker.getTradePrice().floatValue();
        }
    }
}
