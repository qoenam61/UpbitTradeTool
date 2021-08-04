package com.example.upbitautotrade.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upbitautotrade.R;
import com.example.upbitautotrade.appinterface.UpBitTradeActivity;
import com.example.upbitautotrade.model.Candle;
import com.example.upbitautotrade.model.MarketInfo;
import com.example.upbitautotrade.model.Ticker;
import com.example.upbitautotrade.viewmodel.CoinEvaluationViewModel;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_MARKETS_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_MIN_CANDLE_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_TICKER_INFO;

public class CoinMarketDelta extends Fragment {
    public static final String TAG = "CoinMarketDelta";;

    public final String MARKET_NAME_KRW = "KRW";
    public final String MARKET_NAME_USDT = "USDT";
    public final String MARKET_WARNING = "CAUTION";
    private final int MONITOR_MIN_CANDLE_COUNT = 1;

    private View mView;
    private CoinEvaluationViewModel mViewModel;

    private CoinListAdapter mCoinListAdapter;

    private final Map<String, MarketInfo> mUSDTMarketsMapInfo;
    private final Map<String, MarketInfo> mKRWMarketsMapInfo;
    private Map<String, Ticker> mTickerMapInfo;
    private Map<String, DeltaCoinInfo> mMarketDeltaMapInfo;

    private UpBitTradeActivity mActivity;
    private boolean mIsActive = false;

    private double mTetherPrice = 0;
    private double mKRW_BTC = 0;
    private double mUSDT_BTC = 0;

    public CoinMarketDelta() {
        mUSDTMarketsMapInfo = new HashMap<>();
        mKRWMarketsMapInfo = new HashMap<>();
        mTickerMapInfo = new HashMap<>();
        mMarketDeltaMapInfo = new HashMap<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (UpBitTradeActivity)getActivity();
        mViewModel =  mActivity.getCoinEvaluationViewModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_coin_delta, container, false);
        RecyclerView coinList = mView.findViewById(R.id.coin_market_delta_list);
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
                        mUSDTMarketsMapInfo.clear();
                        mKRWMarketsMapInfo.clear();
                        Iterator<MarketInfo> iterator = marketsInfo.iterator();
                        while (iterator.hasNext()) {
                            MarketInfo marketInfo = iterator.next();
                            if (!marketInfo.getMarket_warning().contains(MARKET_WARNING)) {
                                if (marketInfo.getMarketId().contains(MARKET_NAME_KRW + "-")) {
                                    mKRWMarketsMapInfo.put(marketInfo.getMarketId(), marketInfo);
                                } else if (marketInfo.getMarketId().contains(MARKET_NAME_USDT + "-")) {
                                    mUSDTMarketsMapInfo.put(marketInfo.getMarketId(), marketInfo);
                                }
                            }
                        }

                        Set<String> ketSet = new HashSet<>();
                        Iterator<String> stringIterator = mKRWMarketsMapInfo.keySet().iterator();
                        while (stringIterator.hasNext()) {
                            String key = stringIterator.next();
                            String[] ketList = key.split("-");
                            if (mUSDTMarketsMapInfo.containsKey(MARKET_NAME_USDT + "-" + ketList[1])) {
                                ketSet.add(MARKET_NAME_KRW + "-" + ketList[1]);
                                ketSet.add(MARKET_NAME_USDT + "-" + ketList[1]);
                            }
                        }
                        registerPeriodicUpdate(ketSet);
                    }
            );

            mViewModel.getResultTickerInfo().observe(
                    getViewLifecycleOwner(),
                    ticker -> {
                        if (!mIsActive) {
                            return;
                        }
                        updateMonitorKey(ticker);
                    }
            );
        }
    }

    private void registerPeriodicUpdate(Set<String> keySet) {
        Iterator<String> regIterator = keySet.iterator();
        while (regIterator.hasNext()) {
            String key = regIterator.next();
            if (!key.equals("KRW-KRW")) {
                mActivity.getProcessor().registerPeriodicUpdate(UPDATE_TICKER_INFO, key);
            }
        }
    }

    private void updateMonitorKey(List<Ticker> tickersInfo) {
        if (tickersInfo == null || tickersInfo.isEmpty()) {
            return;
        }
        String key = null;
        Iterator<Ticker> iterator = tickersInfo.iterator();
        while (iterator.hasNext()) {
            Ticker ticker = iterator.next();
            key = ticker.getMarketId();
            mTickerMapInfo.put(key, ticker);
        }

        if (key.equals("KRW-BTC")) {
            mKRW_BTC = mTickerMapInfo.get(key).getTradePrice().doubleValue();
        }

        if (key.equals("USDT-BTC")) {
            mUSDT_BTC = mTickerMapInfo.get(key).getTradePrice().doubleValue();
        }

        String[] coinTag = null;
        coinTag = key.split("-");
        if (coinTag != null) {
            String marketId = coinTag[1];
            if (marketId != null) {
                Ticker usdtCandle = mTickerMapInfo.get(MARKET_NAME_USDT + "-" + marketId);
                Ticker krwCandle = mTickerMapInfo.get(MARKET_NAME_KRW + "-" + marketId);
                if (usdtCandle != null && krwCandle != null) {
                    DeltaCoinInfo deltaCoinInfo = new DeltaCoinInfo();
                    deltaCoinInfo.setCoinName(marketId);
                    deltaCoinInfo.setUsdtPrice(usdtCandle.getTradePrice().doubleValue());
                    deltaCoinInfo.setKrwPrice(krwCandle.getTradePrice().doubleValue());
                    mMarketDeltaMapInfo.put(marketId, deltaCoinInfo);

                    List<String> orderList = new ArrayList<>(mMarketDeltaMapInfo.keySet());
                    Collections.sort(orderList, (value1, value2) -> mMarketDeltaMapInfo.get(value1).compareTo(mMarketDeltaMapInfo.get(value2)));
                    mCoinListAdapter.setMonitoringItems(orderList);
                }
            }
        }
        mCoinListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        mIsActive = false;
        mActivity.getProcessor().stopBackgroundProcessor();
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsActive = true;
        mActivity.getProcessor().startBackgroundProcessor();
        mActivity.getProcessor().registerProcess(UPDATE_MARKETS_INFO, null);
    }

    private class CoinHolder extends RecyclerView.ViewHolder {
        public TextView mCoinName;
        public TextView mUSDTPrice;
        public TextView mKRWPrice;
        public TextView mDeltaPrice;
        public TextView mDeltaPriceRate;

        public CoinHolder(View itemView) {
            super(itemView);
            mCoinName = itemView.findViewById(R.id.coin_name);
            mUSDTPrice = itemView.findViewById(R.id.coin_usdt_price);
            mKRWPrice = itemView.findViewById(R.id.coin_krw_price);
            mDeltaPrice = itemView.findViewById(R.id.coin_delta_price);
            mDeltaPriceRate = itemView.findViewById(R.id.coin_delta_price_rate);
        }
    }


    private class CoinListAdapter extends RecyclerView.Adapter<CoinHolder> {
        private DecimalFormat mFormat;
        private DecimalFormat mNonZeroFormat;
        private DecimalFormat mPercentFormat;
        private SimpleDateFormat mTimeFormat;
        private List<String> mCoinListInfo;

        public CoinListAdapter() {
            mFormat = new DecimalFormat("###,###,###,###.#");
            mNonZeroFormat = new DecimalFormat("###,###,###,###");
            mPercentFormat = new DecimalFormat("###.##" + "%");
            mTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
            mTimeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        }

        public void setMonitoringItems(List<String> coinList) {
            mCoinListInfo = coinList;
            notifyDataSetChanged();
        }

        @Override
        public CoinHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.delta_coin_item, parent, false);
            return new CoinHolder(view);
        }

        @Override
        public void onBindViewHolder(CoinHolder holder, int position) {
            String key = mCoinListInfo.get(position);

            MarketInfo marketInfo = mKRWMarketsMapInfo.get(MARKET_NAME_KRW + "-" + key);
            if (marketInfo != null) {
                holder.mCoinName.setText(marketInfo.getKorean_name());
            }

            DeltaCoinInfo deltaCoinInfo = mMarketDeltaMapInfo.get(key);
            if (deltaCoinInfo != null) {
                holder.mUSDTPrice.setText(mFormat.format(deltaCoinInfo.getUsdtPrice()));
                holder.mKRWPrice.setText(mFormat.format(deltaCoinInfo.getKrwPrice()));
                holder.mDeltaPrice.setText(mFormat.format(deltaCoinInfo.getDeltaPrice()));
                holder.mDeltaPriceRate.setText(mPercentFormat.format(deltaCoinInfo.getDeltaRate()));
            }
        }

        @Override
        public int getItemCount() {
            return mCoinListInfo != null ? mCoinListInfo.size() : 0;
        }
    }


    private class DeltaCoinInfo implements Comparable<DeltaCoinInfo> {
        private String coinName;
        private double usdtPrice = 0;
        private double krwPrice = 0;
        double unit = mKRW_BTC / mUSDT_BTC;

        public String getCoinName() {
            return coinName;
        }

        public double getUsdtPrice() {
            return usdtPrice * unit;
        }

        public double getKrwPrice() {
            return krwPrice;
        }

        public double getDeltaPrice() {
            return usdtPrice == 0 || krwPrice == 0 ? 0 : (getUsdtPrice() - krwPrice) ;
        }

        public double getDeltaRate() {
            return usdtPrice == 0 || krwPrice == 0 ? 0 : getDeltaPrice() / krwPrice;
        }

        public void setCoinName(String coinName) {
            this.coinName = coinName;
        }

        public void setUsdtPrice(double usdtPrice) {
            this.usdtPrice = usdtPrice;
        }

        public void setKrwPrice(double krwPrice) {
            this.krwPrice = krwPrice;
        }

        @Override
        public int compareTo(DeltaCoinInfo o) {
            if (this.getDeltaRate() < o.getDeltaRate()) {
                return 1;
            } else if (this.getDeltaRate() > o.getDeltaRate()) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
