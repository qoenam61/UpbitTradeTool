package com.example.upbitautotrade.fragment;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upbitautotrade.R;
import com.example.upbitautotrade.appinterface.UpBitTradeActivity;
import com.example.upbitautotrade.model.Accounts;
import com.example.upbitautotrade.model.Candle;
import com.example.upbitautotrade.model.DayCandle;
import com.example.upbitautotrade.model.MarketInfo;
import com.example.upbitautotrade.model.MonthCandle;
import com.example.upbitautotrade.model.Ticker;
import com.example.upbitautotrade.model.TradeInfo;
import com.example.upbitautotrade.model.WeekCandle;
import com.example.upbitautotrade.utils.BuyingItem;
import com.example.upbitautotrade.viewmodel.CoinEvaluationViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.example.upbitautotrade.utils.BackgroundProcessor.PERIODIC_UPDATE_TICKER_INFO_FOR_EVALUATION;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_MARKETS_INFO_FOR_COIN_EVALUATION;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_MIN_CANDLE_INFO_FOR_COIN_EVALUATION;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_TRADE_INFO_FOR_COIN_EVALUATION;

public class CoinEvaluationFragment extends Fragment {
    public static final String TAG = "CoinEvaluationFragment";;

    public final String MARKET_NAME = "KRW";
    public final String MARKET_WARNING = "CAUTION";

    private final int MONITOR_TICK_COUNTS = 60;
    private final double MONITOR_START_RATE = 0.0001;
    private final double MONITOR_PERIOD_TIME = 30;
    private final double MONITOR_RISING_COUNT = 6;

    private View mView;

    private CoinListAdapter mCoinListAdapter;
    private CoinListAdapter mBuyingListAdapter;
    private UpBitTradeActivity mActivity;
    private CoinEvaluationViewModel mViewModel;

    private List<String> mCoinKeyList;
    private List<String> mMonitorKeyList;
    private List<String> mBuyingItemKeyList;
    private ArrayList mDeadMarketList;
    private Map<String, MarketInfo> mMarketsMapInfo;
    private Map<String, Ticker> mTickerMapInfo;
    private Map<String, Candle> mMinCandleMapInfo;
    private Map<String, DayCandle> mDayCandleMapInfo;
    private Map<String, WeekCandle> mWeekCandleMapInfo;
    private Map<String, MonthCandle> mMonthCandleMapInfo;
    private Map<String, TradeInfo> mTradeMapInfo;
    private Map<String, BuyingItem> mBuyingItemMapInfo;

    boolean mIsStarting = false;

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
        mTradeMapInfo = new HashMap<>();
        mMonitorKeyList = new ArrayList<>();
        mBuyingItemMapInfo = new HashMap<>();
        mBuyingItemKeyList = new ArrayList<>();
    }


    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (UpBitTradeActivity)getActivity();
        mViewModel =  mActivity.getCoinEvaluationViewModel();

        if (savedInstanceState != null) {
            mMarketsMapInfo = (HashMap<String, MarketInfo>) savedInstanceState.getSerializable("marketMapInfo");
            mTickerMapInfo = (HashMap<String, Ticker>) savedInstanceState.getSerializable("tickerMapInfo");
            mMinCandleMapInfo = (HashMap<String, Candle>) savedInstanceState.getSerializable("minCandleInfo");
            mDayCandleMapInfo = (HashMap<String, DayCandle>) savedInstanceState.getSerializable("dayCandleInfo");
            mWeekCandleMapInfo = (HashMap<String, WeekCandle>) savedInstanceState.getSerializable("weekCandleInfo");
            mMonthCandleMapInfo = (HashMap<String, MonthCandle>) savedInstanceState.getSerializable("monthCandleInfo");
            mCoinKeyList = (List<String>) savedInstanceState.getStringArrayList("coinKeyList");
            mDeadMarketList = (ArrayList) savedInstanceState.getStringArrayList("deadMarketList");
            mTradeMapInfo = (HashMap<String, TradeInfo>) savedInstanceState.getSerializable("tradeMapInfo");
            mMonitorKeyList = (List<String>) savedInstanceState.getStringArrayList("monitorKeyList");
            mBuyingItemMapInfo = (HashMap<String, BuyingItem>) savedInstanceState.getSerializable("buyingItemMapInfo");
            mBuyingItemKeyList = (List<String>) savedInstanceState.getStringArrayList("buyingItemKeyList");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        outState.putSerializable("marketMapInfo", (Serializable) mMarketsMapInfo);
        outState.putSerializable("tickerMapInfo", (Serializable) mTickerMapInfo);
        outState.putSerializable("minCandleInfo", (Serializable) mMinCandleMapInfo);
        outState.putSerializable("dayCandleInfo", (Serializable) mDayCandleMapInfo);
        outState.putSerializable("weekCandleInfo", (Serializable) mWeekCandleMapInfo);
        outState.putSerializable("monthCandleInfo", (Serializable) mMonthCandleMapInfo);
        outState.putStringArrayList("coinKeyList", (ArrayList<String>) mCoinKeyList);
        outState.putStringArrayList("deadMarketList", mDeadMarketList);
        outState.putSerializable("tradeMapInfo", (Serializable) mTradeMapInfo);
        outState.putStringArrayList("monitorKeyList", (ArrayList<String>) mMonitorKeyList);
        outState.putSerializable("buyingItemMapInfo", (Serializable) mBuyingItemMapInfo);
        outState.putStringArrayList("buyingItemKeyList", (ArrayList<String>) mBuyingItemKeyList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_coin_evaluation, container, false);
        RecyclerView coinList = mView.findViewById(R.id.coin_evaluation_list);
        RecyclerView buyingList = mView.findViewById(R.id.coin_buying_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        coinList.setLayoutManager(layoutManager);
        mCoinListAdapter = new CoinListAdapter(true);
        coinList.setAdapter(mCoinListAdapter);

        LinearLayoutManager layoutBuyManager = new LinearLayoutManager(getContext());
        buyingList.setLayoutManager(layoutBuyManager);
        mBuyingListAdapter = new CoinListAdapter(false);
        buyingList.setAdapter(mBuyingListAdapter);

        Button startButton = mView.findViewById(R.id.start_button);
        startButton.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
        startButton.setOnClickListener(l -> {
            mIsStarting = true;
            startButton.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
        });
        Button endButton = mView.findViewById(R.id.stop_button);
        endButton.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
        endButton.setOnClickListener(l -> {
            mIsStarting = false;
            startButton.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
        });
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
                            mTickerMapInfo.put(tick.getMarketId(), tick);
                        }
                        mCoinListAdapter.setMonitoringItems(mMonitorKeyList);
                        mCoinListAdapter.notifyDataSetChanged();
                        mBuyingListAdapter.notifyDataSetChanged();
                    }
            );

            mViewModel.getMinCandleInfo().observe(
                    getViewLifecycleOwner(),
                    minCandles -> {
                        updateMonitorKey(minCandles);
                    }
            );

            mViewModel.getMonthCandleInfo().observe(
                    getViewLifecycleOwner(),
                    monthCandle -> {
                        Iterator<MonthCandle> iterator = monthCandle.iterator();
                        while (iterator.hasNext()) {
                            MonthCandle candle = iterator.next();
                            mMonthCandleMapInfo.put(candle.getMarketId(), candle);
                        }
                    }
            );

            mViewModel.getTradeInfo().observe(
                    getViewLifecycleOwner(),
                    tradesInfo -> {
                        mappingTradeMapInfo(tradesInfo);
                    }
            );
        }
    }

    private void mappingTradeMapInfo(List<TradeInfo> tradesInfo) {
        if (tradesInfo == null || tradesInfo.isEmpty()) {
            return;
        }
        Iterator<TradeInfo> iterator = tradesInfo.iterator();
        TradeInfo newTradeInfo = null;
        TradeInfo prevTradeInfo = null;
        String key = null;
        int i = 0;
        while (iterator.hasNext()) {
            TradeInfo tradeInfo = iterator.next();
            key = tradeInfo.getMarketId();
            prevTradeInfo = mTradeMapInfo.get(key);
            if (prevTradeInfo == null) {
                if (i == 0) {
                    newTradeInfo = tradeInfo;
                    newTradeInfo.setEndTime(tradeInfo.getTimestamp());
                    newTradeInfo.setMonitoringStartTime(0);
                    newTradeInfo.setTickCount(0);
                    newTradeInfo.setRisingCount(0);
                }
                newTradeInfo.setStartTime(tradeInfo.getTimestamp());
                newTradeInfo.setTickCount(newTradeInfo.getTickCount() + 1);
                if (newTradeInfo.getTickCount() == MONITOR_TICK_COUNTS) {
                    newTradeInfo.setTickCount(0);
                    newTradeInfo.setStartTime(tradeInfo.getTimestamp());
                    float changedPrice = newTradeInfo.getTradePrice().floatValue() - tradeInfo.getTradePrice().floatValue();
                    float prevPrice = tradeInfo.getTradePrice().floatValue();;
                    float rate = changedPrice / prevPrice;
                    if (rate >= MONITOR_START_RATE) {
                        newTradeInfo.setRisingCount(1);
                    } else if (rate < MONITOR_START_RATE) {
                        newTradeInfo.setRisingCount(-1);
                    }
                }
            } else {
                if (tradeInfo.getSequentialId() < prevTradeInfo.getSequentialId()) {
                    continue;
                }
                if (i == 0) {
                    newTradeInfo = tradeInfo;
                    newTradeInfo.setMonitoringStartTime(prevTradeInfo.getMonitoringStartTime());
                    newTradeInfo.setEndTime(tradeInfo.getTimestamp());
                    newTradeInfo.setStartTime(-1);
                    newTradeInfo.setTickCount(prevTradeInfo.getTickCount());
                    newTradeInfo.setRisingCount(prevTradeInfo.getRisingCount());
                }
                newTradeInfo.setTickCount(newTradeInfo.getTickCount() + 1);

                if (newTradeInfo.getTickCount() == MONITOR_TICK_COUNTS) {
                    newTradeInfo.setTickCount(0);
                    newTradeInfo.setStartTime(tradeInfo.getTimestamp());
                    if (newTradeInfo.getEndTime() - newTradeInfo.getStartTime() < MONITOR_PERIOD_TIME * 1000) {
                        float changedPrice = newTradeInfo.getTradePrice().floatValue() - prevTradeInfo.getTradePrice().floatValue();
                        float prevPrice = prevTradeInfo.getTradePrice().floatValue();
                        float rate = changedPrice / prevPrice;
                        if (rate >= MONITOR_START_RATE) {
                            if (newTradeInfo.getRisingCount() == 0) {
                                newTradeInfo.setMonitoringStartTime(tradeInfo.getTimestamp());
                            }
                            newTradeInfo.setRisingCount(prevTradeInfo.getRisingCount() + 1);
                        } else if (rate < MONITOR_START_RATE) {
                            newTradeInfo.setRisingCount(prevTradeInfo.getRisingCount() - 1);
                            if (newTradeInfo.getRisingCount() < 0) {
                                newTradeInfo.setMonitoringStartTime(0);
                            }
                        } else {
                            newTradeInfo.setRisingCount(0);
                            newTradeInfo.setMonitoringStartTime(0);
                        }
                    }
                    if (newTradeInfo.getRisingCount() > MONITOR_RISING_COUNT && newTradeInfo.getEndTime() - newTradeInfo.getMonitoringStartTime() < MONITOR_PERIOD_TIME * 2 * 1000) {
                        //Buy
                        if (!mBuyingItemKeyList.contains(key)) {
                            mBuyingItemKeyList.add(key);
                            BuyingItem item = new BuyingItem();
                            item.setMarketId(key);
                            item.setBuyingPrice(newTradeInfo.getTradePrice().intValue());
                            item.setBuyingAmount(5000000);
                            mBuyingItemMapInfo.put(key, item);
                            mBuyingListAdapter.setBuyingItems(mBuyingItemKeyList);
                            mBuyingListAdapter.notifyDataSetChanged();
                        }
                        Log.d(TAG, "[DEBUG] BUY - market: "+newTradeInfo.getMarketId()+" BuyPrice: "+newTradeInfo.getTradePrice());
                    } else if (newTradeInfo.getEndTime() - newTradeInfo.getMonitoringStartTime() > MONITOR_PERIOD_TIME * 2 * 1000) {
                        newTradeInfo.setRisingCount(0);
                        newTradeInfo.setMonitoringStartTime(0);
                    }
                }
            }
            i++;
        }
        mTradeMapInfo.put(key, newTradeInfo);

        DateFormat format = new SimpleDateFormat("HH:mm:ss");
        Log.d(TAG, "[DEBUG] getEvaluationTradeInfo - "
                + " getMarketId: " + mTradeMapInfo.get(key).getMarketId()
                + " getSequentialId: " + mTradeMapInfo.get(key).getSequentialId()
                + " time: "+format.format(mTradeMapInfo.get(key).getTimestamp())
                +" getRisingCount: "+mTradeMapInfo.get(key).getRisingCount()
                +" tickCount: "+mTradeMapInfo.get(key).getTickCount()
                +" getStartTime: "+format.format(mTradeMapInfo.get(key).getStartTime())
                +" getEndTime: "+format.format(mTradeMapInfo.get(key).getEndTime())
                +" getMonitoringStartTime: "+format.format(mTradeMapInfo.get(key).getMonitoringStartTime())
        );

    }

    private void updateMonitorKey(List<Candle> minCandlesInfo) {
        if (minCandlesInfo == null || minCandlesInfo.isEmpty()) {
            return;
        }
        float[] tradePrice = new float[2];
        int i = 0;
        String key = null;
        Iterator<Candle> iterator = minCandlesInfo.iterator();
        while (iterator.hasNext()) {
            Candle candle = iterator.next();
            key = candle.getMarketId();
            if (i == 0) {
                mMinCandleMapInfo.put(key, candle);
            }
            tradePrice[i] = candle.getTradePrice().intValue();
            i++;
        }

        float changedPrice = tradePrice[0] - tradePrice[1];
        float prevPrice = tradePrice[1];

        mMinCandleMapInfo.get(key).setChangedPrice((int) changedPrice);
        mMinCandleMapInfo.get(key).setChangedRate(prevPrice != 0 ? (changedPrice / prevPrice) : 0);

        if (prevPrice != 0 && (changedPrice / prevPrice) > MONITOR_START_RATE) {
            if (!mMonitorKeyList.contains(key)) {
                removeMonitoringPeriodicUpdate();
                mMonitorKeyList.add(key);
                registerPeriodicUpdate(mMonitorKeyList);
                mCoinListAdapter.setMonitoringItems(mMonitorKeyList);
                mCoinListAdapter.notifyDataSetChanged();
            }
        } else if (prevPrice != 0 && changedPrice / prevPrice < MONITOR_START_RATE * -2) {
            if (mMonitorKeyList.contains(key)) {
                removeMonitoringPeriodicUpdate();
                mMonitorKeyList.remove(key);
                registerPeriodicUpdate(mMonitorKeyList);
                mCoinListAdapter.setMonitoringItems(mMonitorKeyList);
                mCoinListAdapter.notifyDataSetChanged();
                mTickerMapInfo.remove(key);
                mTradeMapInfo.remove(key);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!mIsStarting) {
            mActivity.getProcessor().removePeriodicUpdate(PERIODIC_UPDATE_TICKER_INFO_FOR_EVALUATION);
        }
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
                mActivity.getProcessor().registerPeriodicUpdate(1, key, UPDATE_MIN_CANDLE_INFO_FOR_COIN_EVALUATION, null, 2);
            }
        }
    }

    private void registerPeriodicUpdate(List<String> monitorKeyList) {
        Iterator<String> monitorIterator = monitorKeyList.iterator();
        while (monitorIterator.hasNext()) {
            String key = monitorIterator.next();
            if (!key.equals("KRW-KRW")) {
                mActivity.getProcessor().registerPeriodicUpdate(key, PERIODIC_UPDATE_TICKER_INFO_FOR_EVALUATION);
                mActivity.getProcessor().registerPeriodicUpdate(key, UPDATE_TRADE_INFO_FOR_COIN_EVALUATION, null, MONITOR_TICK_COUNTS);
            }
        }
    }

    private void removeMonitoringPeriodicUpdate() {
        if (!mIsStarting) {
            mActivity.getProcessor().removePeriodicUpdate(PERIODIC_UPDATE_TICKER_INFO_FOR_EVALUATION);
            mActivity.getProcessor().removePeriodicUpdate(UPDATE_TRADE_INFO_FOR_COIN_EVALUATION);
        }
    }

    private class CoinHolder extends RecyclerView.ViewHolder {
        public TextView mCoinName;
        public TextView mCurrentPrice;
        public TextView mRatePerMin;
        public TextView mTickAmount;
        public TextView mAmountPerMin;
        public TextView mChangeRate;
        public TextView mBuyingPrice;
        public TextView mBuyingAmount;

        public CoinHolder(@NonNull @NotNull View itemView, boolean isMonitor) {
            super(itemView);
            if (isMonitor) {
                mCoinName = itemView.findViewById(R.id.coin_name);
                mCurrentPrice = itemView.findViewById(R.id.coin_current_price);
                mRatePerMin = itemView.findViewById(R.id.coin_change_rate);
                mTickAmount = itemView.findViewById(R.id.buying_price);
                mAmountPerMin = itemView.findViewById(R.id.buying_amount);
            } else {
                mCoinName = itemView.findViewById(R.id.coin_name);
                mCurrentPrice = itemView.findViewById(R.id.coin_current_price);
                mChangeRate = itemView.findViewById(R.id.coin_change_rate);
                mBuyingPrice = itemView.findViewById(R.id.buying_price);
                mBuyingAmount = itemView.findViewById(R.id.buying_amount);
            }
        }
    }

    private class CoinListAdapter extends RecyclerView.Adapter<CoinHolder> {
        private DecimalFormat mFormat;
        private DecimalFormat mNonZeroFormat;
        private DecimalFormat mPercentFormat;
        private List<String> mCoinListInfo;
        private List<String> mBuyingListInfo;
        private boolean mIsMonitor;

        public CoinListAdapter(boolean isMonitor) {
            mIsMonitor = isMonitor;
            mFormat = new DecimalFormat("###,###,###,###.#");
            mNonZeroFormat = new DecimalFormat("###,###,###,###");
            mPercentFormat = new DecimalFormat("###.##" + "%");
        }

        public void setMonitoringItems(List<String> coinList) {
            mCoinListInfo = coinList;
            notifyDataSetChanged();
        }

        public void setBuyingItems(List<String> coinList) {
            mBuyingListInfo = coinList;
            notifyDataSetChanged();
        }

        @Override
        public CoinHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (mIsMonitor) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.evaluation_coin_item, parent, false);
            } else {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.evaluation_buying_coin_item, parent, false);
            }
            return new CoinHolder(view, mIsMonitor);
        }

        @Override
        public void onBindViewHolder(CoinHolder holder, int position) {
            if (mIsMonitor) {
                String key = mCoinListInfo.get(position);
                MarketInfo marketInfo = mMarketsMapInfo.get(key);
                if (marketInfo != null) {
                    holder.mCoinName.setText(marketInfo.getKorean_name());
                }
                Ticker ticker = mTickerMapInfo.get(key);
                if (ticker != null) {
                    holder.mCurrentPrice.setText(mNonZeroFormat.format(ticker.getTradePrice().intValue()));
                    holder.mTickAmount.setText(mFormat.format(ticker.getTradeVolume().doubleValue()
                            * ticker.getTradePrice().doubleValue() / 1000000));
                }

                Candle candle = mMinCandleMapInfo.get(key);
                if (candle != null) {
                    String amount = mFormat.format(candle.getCandleAccTradeVolume().doubleValue()
                            * candle.getTradePrice().doubleValue() / 1000000);
                    holder.mRatePerMin.setText(mPercentFormat.format(candle.getChangedRate()));
                    holder.mAmountPerMin.setText(amount);
                }
            } else {
                String key = mBuyingListInfo.get(position);
                MarketInfo marketInfo = mMarketsMapInfo.get(key);
                if (marketInfo != null) {
                    holder.mCoinName.setText(marketInfo.getKorean_name());
                }
                holder.mCoinName.setText(marketInfo.getKorean_name());

                Ticker ticker = mTickerMapInfo.get(key);
                int currentPrice = 0;
                if (ticker != null) {
                    currentPrice = ticker.getTradePrice().intValue();
                    holder.mCurrentPrice.setText(mNonZeroFormat.format(currentPrice));
                }
                BuyingItem buyingItem = mBuyingItemMapInfo.get(key);
                if (buyingItem != null) {
                    holder.mBuyingPrice.setText(mNonZeroFormat.format(buyingItem.getBuyingPrice()));
                    int changedPrice = currentPrice - buyingItem.getBuyingPrice();
                    int prevPrice = buyingItem.getBuyingPrice();
                    float rate = prevPrice != 0 ? (changedPrice / (float)prevPrice) : 0;
                    holder.mChangeRate.setText(mPercentFormat.format(rate));
                    holder.mBuyingAmount.setText(mNonZeroFormat.format(buyingItem.getBuyingAmount() * rate));
                }
            }
        }

        @Override
        public int getItemCount() {
            int count = 0;
            if (mIsMonitor) {
                count = mCoinListInfo != null ? mCoinListInfo.size() : 0;
            } else {
                count = mBuyingListInfo != null ? mBuyingListInfo.size() : 0;
            }
            return count;
        }
    }
}
