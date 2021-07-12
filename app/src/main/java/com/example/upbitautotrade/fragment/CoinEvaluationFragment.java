package com.example.upbitautotrade.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upbitautotrade.R;
import com.example.upbitautotrade.appinterface.UpBitTradeActivity;
import com.example.upbitautotrade.model.Candle;
import com.example.upbitautotrade.model.DayCandle;
import com.example.upbitautotrade.model.MarketInfo;
import com.example.upbitautotrade.model.MonthCandle;
import com.example.upbitautotrade.model.Ticker;
import com.example.upbitautotrade.model.WeekCandle;
import com.example.upbitautotrade.viewmodel.CoinEvaluationViewModel;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.example.upbitautotrade.utils.BackgroundProcessor.PERIODIC_UPDATE_TICKER_INFO_FOR_EVALUATION;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_MARKETS_INFO_FOR_COIN_EVALUATION;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_MONTH_CANDLE_INFO_FOR_COIN_EVALUATION;

public class CoinEvaluationFragment extends Fragment {
    public static final String TAG = "CoinEvaluationFragment";;

    public final String MARKET_NAME = "KRW";
    public final String MARKET_WARNING = "CAUTION";
    private final ArrayList mDeadMarketList;

    private View mView;

    private CoinListAdapter mCoinListAdapter;
    private UpBitTradeActivity mActivity;
    private CoinEvaluationViewModel mViewModel;

    private List<String> mCoinKeyList;
    private final HashMap<String, MarketInfo> mMarketsMapInfo;
    private final HashMap<String, Ticker> mTickerMapInfo;
    private final HashMap<String, Candle> mMinCandleMapInfo;
    private final HashMap<String, DayCandle> mDayCandleMapInfo;
    private final HashMap<String, WeekCandle> mWeekCandleMapInfo;
    private final HashMap<String, MonthCandle> mMonthCandleMapInfo;

    private String[] deadMarket = {
            "KRW-NEO", "KRW-MTL", "KRW-OMG", "KRW-SNT", "KRW-WAVES",
            "KRW-XEM", "KRW-QTUM", "KRW-LSK", "KRW-ARDR", "KRW-ARK",
            "KRW-STORJ", "KRW-GRS", "KRW-REP", "KRW-SBD", "KRW-POWR",
            "KRW-BTG", "KRW-ICX", "KRW-SC", "KRW-ONT", "KRW-ZIL",
            "KRW-POLY", "KRW-ZRX", "KRW-LOOM", "KRW-BAT", "KRW-IOST",
            "KRW-RFR", "KRW-CVC", "KRW-IQ", "KRW-IOTA", "KRW-MFT",
            "KRW-GAS", "KRW-ELF", "KRW-KNC", "KRW-BSV", "KRW-QKC",
            "KRW-BTT", "KRW-MOC", "KRW-ENJ", "KRW-TFUEL", "KRW-ANKR",
            "KRW-AERGO", "KRW-ATOM", "KRW-TT", "KRW-CRE", "KRW-MBL",
            "KRW-WAXP", "KRW-HBAR", "KRW-MED", "KRW-MLK", "KRW-STPT",
            "KRW-ORBS", "KRW-CHZ", "KRW-STMX", "KRW-DKA", "KRW-HIVE",
            "KRW-KAVA", "KRW-AHT", "KRW-XTZ", "KRW-BORA", "KRW-JST",
            "KRW-TON", "KRW-SXP", "KRW-HUNT", "KRW-PLA", "KRW-SRM",
            "KRW-MVL", "KRW-STRAX", "KRW-AQT", "KRW-BCHA", " KRW-GLM",
            "KRW-SSX", "KRW-META", "KRW-FCT2", "KRW-HUM", "KRW-STRK",
            "KRW-PUNDIX", "KRW-STX",
    };

    public CoinEvaluationFragment() {
        mMarketsMapInfo = new HashMap<>();
        mTickerMapInfo = new HashMap<>();
        mMinCandleMapInfo = new HashMap<>();
        mDayCandleMapInfo = new HashMap<>();
        mWeekCandleMapInfo = new HashMap<>();
        mMonthCandleMapInfo = new HashMap<>();
        mCoinKeyList = new ArrayList<>();
        mDeadMarketList = new ArrayList(Arrays.asList(deadMarket));

    }


    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (UpBitTradeActivity)getActivity();
        mViewModel =  mActivity.getCoinEvaluationViewModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_coin_evaluation, container, false);
        RecyclerView coinList = mView.findViewById(R.id.coin_evaluation_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        coinList.setLayoutManager(layoutManager);
        mCoinListAdapter = new CoinListAdapter();
        coinList.setAdapter(mCoinListAdapter);
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mActivity.getProcessor().registerProcess(null, UPDATE_MARKETS_INFO_FOR_COIN_EVALUATION);
        if (mViewModel != null) {
            mViewModel.getMarketsInfo().observe(
                    getViewLifecycleOwner(),
                    marketsInfo -> {
                        mMarketsMapInfo.clear();
                        mCoinKeyList.clear();
                        Iterator<MarketInfo> iterator = marketsInfo.iterator();
                        while (iterator.hasNext()) {
                            MarketInfo marketInfo = iterator.next();
                            if (marketInfo.getMarket().contains(MARKET_NAME+"-")
                                    && !marketInfo.getMarket_warning().contains(MARKET_WARNING)) {
                                if (mDeadMarketList.contains(marketInfo.getMarket())) {
                                    continue;
                                }
                                mMarketsMapInfo.put(marketInfo.getMarket(), marketInfo);
                                mCoinKeyList.add(marketInfo.getMarket());

                                /*
                                Log.d(TAG, "[DEBUG] onStart: UPDATE_MONTH_CANDLE_INFO_FOR_COIN_EVALUATION");
                                mActivity.getProcessor().registerProcess(
                                        marketInfo.getMarket(),
                                        UPDATE_MONTH_CANDLE_INFO_FOR_COIN_EVALUATION,
                                        null,
                                        1);
                                */
                            }
                        }
                        registerPeriodicUpdate(mMarketsMapInfo.keySet());
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
                        Log.d(TAG, "[DEBUG] onStart: ticker");
                        mCoinListAdapter.setItems(mCoinKeyList);
                        mCoinListAdapter.notifyDataSetChanged();
                    }
            );

            mViewModel.getMonthCandleInfo().observe(
                    getViewLifecycleOwner(),
                    monthCandle -> {
                        Iterator<MonthCandle> iterator = monthCandle.iterator();
                        while (iterator.hasNext()) {
                            MonthCandle monthInfo = iterator.next();
                            Log.d(TAG, "[DEBUG] onStart: monthInfo: "+monthInfo.getMarketId());
                            mMonthCandleMapInfo.put(monthInfo.getMarketId(), monthInfo);
                        }
                    }
            );
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mActivity.getProcessor().removePeriodicUpdate(PERIODIC_UPDATE_TICKER_INFO_FOR_EVALUATION);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void registerPeriodicUpdate(Set<String> keySet) {
        Iterator<String> regIterator = keySet.iterator();
        while (regIterator.hasNext()) {
            String key = regIterator.next();
            if (!key.equals("KRW-KRW")) {
                mActivity.getProcessor().registerPeriodicUpdate(key, PERIODIC_UPDATE_TICKER_INFO_FOR_EVALUATION);
            }
        }
    }

    private void updateKeySet(List<String> keySet, MonthCandle monthCandle) {
        Iterator<String> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (key.equals(monthCandle.getMarketId())) {
                Double tradeAmount = (monthCandle.getCandleAccTradePrice().doubleValue() / 100000000);
                if (tradeAmount < 10000 && monthCandle.getTradePrice().intValue() < 100) {
                    iterator.remove();
                    Log.d(TAG, "[DEBUG] updateKeySet -getCandleAccTradePrice: "
                            + monthCandle.getMarketId()
                            + " price: "
                            + monthCandle.getTradePrice().intValue());
                }
            }
        }

    }

    private class CoinHolder extends RecyclerView.ViewHolder {
        public TextView mCoinName;
        public TextView mCurrentPrice;
        public TextView mRatePerMin;
        public TextView mTickAmount;
        public TextView mAmountPerMin;

        public CoinHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mCoinName = itemView.findViewById(R.id.coin_name);
            mCurrentPrice = itemView.findViewById(R.id.coin_current_price);
            mRatePerMin = itemView.findViewById(R.id.coin_variant_rate);
            mTickAmount = itemView.findViewById(R.id.coin_tick_amount);
            mAmountPerMin = itemView.findViewById(R.id.coin_amount);
        }
    }

    private class CoinListAdapter extends RecyclerView.Adapter<CoinHolder> {
        private DecimalFormat mFormat;
        private DecimalFormat mNonZeroFormat;
        private DecimalFormat mPercentFormat;
        private List<String> mCoinListInfo;

        public void setItems(List<String> coinList) {
            mCoinListInfo = coinList;
            notifyDataSetChanged();
            mFormat = new DecimalFormat("###,###,###,###.###");
            mNonZeroFormat = new DecimalFormat("###,###,###,###");
            mPercentFormat = new DecimalFormat("###.##" + "%");
        }

        @Override
        public CoinHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.evaluation_coin_item, parent, false);
            return new CoinHolder(view);
        }

        @Override
        public void onBindViewHolder(CoinHolder holder, int position) {
            MarketInfo marketInfo = mMarketsMapInfo.get(mCoinListInfo.get(position));
            Ticker ticker = mTickerMapInfo.get(mCoinListInfo.get(position));
            if (marketInfo == null) {
                return;
            }
            holder.mCoinName.setText(marketInfo.getKorean_name());

            if (ticker == null) {
                Log.d(TAG, "[DEBUG] onBindViewHolder: ticker == null");
                return;
            }
            Log.d(TAG, "[DEBUG] onBindViewHolder: getTradePrice "+ticker.getTradePrice().intValue());
            holder.mCurrentPrice.setText(mNonZeroFormat.format(ticker.getTradePrice().intValue()));
        }

        @Override
        public int getItemCount() {
            return mCoinListInfo != null ? mCoinListInfo.size() : 0;
        }
    }
}
