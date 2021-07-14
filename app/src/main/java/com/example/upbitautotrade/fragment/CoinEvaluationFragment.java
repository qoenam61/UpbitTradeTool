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
import com.example.upbitautotrade.model.TradeInfo;
import com.example.upbitautotrade.model.WeekCandle;
import com.example.upbitautotrade.viewmodel.CoinEvaluationViewModel;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
    private final ArrayList mDeadMarketList;
    private final int MONITOR_TICK_COUNTS = 10;
    private final double MONITOR_START_RATE = 0.0005;


    private View mView;

    private CoinListAdapter mCoinListAdapter;
    private UpBitTradeActivity mActivity;
    private CoinEvaluationViewModel mViewModel;

    private List<String> mCoinKeyList;
    private List<String> mMonitorKeyList;
    private final Map<String, MarketInfo> mMarketsMapInfo;
    private final Map<String, Ticker> mTickerMapInfo;
    private final Map<String, Candle> mMinCandleMapInfo;
    private final Map<String, DayCandle> mDayCandleMapInfo;
    private final Map<String, WeekCandle> mWeekCandleMapInfo;
    private final Map<String, MonthCandle> mMonthCandleMapInfo;
    private final Map<String, TradeInfo> mTradeMapInfo;

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
                        mCoinListAdapter.setItems(mMonitorKeyList);
                        mCoinListAdapter.notifyDataSetChanged();
                    }
            );

            mViewModel.getMinCandleInfo().observe(
                    getViewLifecycleOwner(),
                    minCandles -> {
                        Iterator<Candle> iterator = minCandles.iterator();
                        while (iterator.hasNext()) {
                            Candle candle = iterator.next();
                            mMinCandleMapInfo.put(candle.getMarketId(), candle);
                        }
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




//                        getEvaluationTradeInfo(tradesInfo);
//
//                        Iterator<TradeInfo> iterator = tradesInfo.iterator();
//                        while (iterator.hasNext()) {
//                            TradeInfo tradeInfo = iterator.next();
//                        }

//                        mTradeMapInfo.put(tradeInfo.getMarketId(), tradeInfo);
//
//                        TradeInfo tradeInfo = getEvaluationTradeInfo(tradesInfo);

/*                        DateFormat format = new SimpleDateFormat("HH:mm:ss");
                        Iterator<TradeInfo> iterator = tradesInfo.iterator();
                        while (iterator.hasNext()) {
                            TradeInfo tradeInfo = iterator.next();
//                            Log.d(TAG, "[DEBUG] MARKET: "+tradeInfo.getMarketId()
//                                    + " DATE: "+tradeInfo.getTradeDateUtc()
//                                    + " TIME: "+tradeInfo.getTradeTimeUtc()
//                                    + " Price: "+tradeInfo.getTradePrice()
//                                    + " Volume: "+tradeInfo.getTradeVolume()
//                                    + " PrevClosingPrice: "+tradeInfo.getPrevClosingPrice()
//                                    + " RATE: "+tradeInfo.getChangePrice()
//                                    + " ASK/BID: "+tradeInfo.getAskBid()
//                                    + " SequentialId: "+tradeInfo.getSequentialId()
//                            );
                        }
                        TradeInfo tradeInfo = getEvaluationTradeInfo(tradesInfo);
                        mTradeMapInfo.put(tradeInfo.getMarketId(), tradeInfo);
//                        Log.d(TAG, "[DEBUG] MARKET- Result: "+tradeInfo.getMarketId()
//                                + " DATE: "+tradeInfo.getTradeDateUtc()
//                                + " TIME: "+tradeInfo.getTradeTimeUtc()
//                                + " timeStamp: "+format.format(tradeInfo.getTimestamp())
//                                + " Price: "+tradeInfo.getTradePrice()
//                                + " Volume: "+tradeInfo.getTradeVolume()
//                                + " PrevClosingPrice: "+tradeInfo.getPrevClosingPrice()
//                                + " RATE: "+tradeInfo.getChangePrice()
//                                + " ASK/BID: "+tradeInfo.getAskBid()
//                                + " SequentialId: "+tradeInfo.getSequentialId()
//                                + " startTime: "+format.format(tradeInfo.getStartTime())
//                                + " endTime: "+format.format(tradeInfo.getEndTime())
//                                + " diff0: "+(tradeInfo.getEndTime() - tradeInfo.getStartTime())
//                                + " diff1: "+format.format(tradeInfo.getEndTime() - tradeInfo.getStartTime())
//                                + " RisingCount: "+tradeInfo.getTickCount());
                    */
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
                newTradeInfo.setTickCount(newTradeInfo.getTickCount() + 1);
                if (newTradeInfo.getTickCount() == MONITOR_TICK_COUNTS) {
                    newTradeInfo.setTickCount(0);
                    newTradeInfo.setStartTime(tradeInfo.getTimestamp());
                    double changedPrice = newTradeInfo.getTradePrice().doubleValue() - tradeInfo.getTradePrice().doubleValue();
                    double prevPrice = tradeInfo.getTradePrice().doubleValue();;
                    float rate = (float) (changedPrice / prevPrice);
                    if (rate >= MONITOR_START_RATE) {
                        newTradeInfo.setRisingCount(1);
                    } else if (rate < MONITOR_START_RATE * -2) {
                        newTradeInfo.setRisingCount(-1);
                    }
                }
            } else {
                if (tradeInfo.getSequentialId() < prevTradeInfo.getSequentialId()) {
                    continue;
                }
                if (i == 0) {
                    newTradeInfo = tradeInfo;
                    newTradeInfo.setTickCount(prevTradeInfo.getTickCount());
                    newTradeInfo.setEndTime(tradeInfo.getTimestamp());
                    if (prevTradeInfo.getRisingCount() > 0) {
                        newTradeInfo.setMonitoringStartTime(prevTradeInfo.getMonitoringStartTime());
                    } else {
                        newTradeInfo.setMonitoringStartTime(0);
                    }
                    newTradeInfo.setRisingCount(prevTradeInfo.getRisingCount());
                }
                newTradeInfo.setTickCount(newTradeInfo.getTickCount() + 1);
                if (newTradeInfo.getTickCount() == MONITOR_TICK_COUNTS) {
                    newTradeInfo.setTickCount(0);
                    newTradeInfo.setStartTime(tradeInfo.getTimestamp());
                    double changedPrice = newTradeInfo.getTradePrice().doubleValue() - prevTradeInfo.getTradePrice().doubleValue();
                    double prevPrice = prevTradeInfo.getTradePrice().doubleValue();;
                    float rate = (float) (changedPrice / prevPrice);
                    if (rate >= MONITOR_START_RATE) {
                        newTradeInfo.setRisingCount(prevTradeInfo.getRisingCount() + 1);
                    } else if (rate < MONITOR_START_RATE * -2) {
                        newTradeInfo.setRisingCount(prevTradeInfo.getRisingCount() - 1);
                    }
                }
                DateFormat format = new SimpleDateFormat("HH:mm:ss");
                Log.d(TAG, "[DEBUG] getEvaluationTradeInfo - "
                        + " getMarketId: " + newTradeInfo.getMarketId()
                        + " getSequentialId: " + newTradeInfo.getSequentialId()
                        + " time: "+format.format(newTradeInfo.getTimestamp())
                        +" getRisingCount: "+newTradeInfo.getRisingCount()
                        +" tickCount: "+newTradeInfo.getTickCount()
                        +" getStartTime: "+newTradeInfo.getStartTime()
                        +" getEndTime: "+newTradeInfo.getEndTime()
                        +" getMonitoringStartTime: "+newTradeInfo.getMonitoringStartTime()
                );
            }
            i++;
        }

        mTradeMapInfo.put(key, newTradeInfo);

/*        mTradeMapInfo.put(key, newTradeInfo);
        DateFormat format = new SimpleDateFormat("HH:mm:ss");
        Log.d(TAG, "[DEBUG] getEvaluationTradeInfo - "
                + " getMarketId: " + mTradeMapInfo.get(key).getMarketId()
                + " getSequentialId: " + mTradeMapInfo.get(key).getSequentialId()
                + " time: "+format.format(mTradeMapInfo.get(key).getTimestamp())
                +" getRisingCount: "+mTradeMapInfo.get(key).getRisingCount()
                +" tickCount: "+mTradeMapInfo.get(key).getTickCount()
                +" getStartTime: "+mTradeMapInfo.get(key).getStartTime()
                +" getEndTime: "+mTradeMapInfo.get(key).getEndTime()
                +" getMonitoringStartTime: "+mTradeMapInfo.get(key).getMonitoringStartTime()
        );
        */
    }

    private void updateMonitorKey(List<Candle> minCandlesInfo) {
        if (minCandlesInfo == null || minCandlesInfo.isEmpty()) {
            return;
        }
        float[] tradePrice = new float[2];
        int i = 0;
        String marketId = null;
        Iterator<Candle> iterator = minCandlesInfo.iterator();
        while (iterator.hasNext()) {
            Candle candle = iterator.next();
            marketId = candle.getMarketId();
            tradePrice[i] = candle.getTradePrice().intValue();
            i++;
        }

        float changedPrice = tradePrice[0] - tradePrice[1];
        float prevPrice = tradePrice[1];

        if (prevPrice != 0 && (changedPrice / prevPrice) > MONITOR_START_RATE) {
            if (!mMonitorKeyList.contains(marketId)) {
                removeMonitoringPeriodicUpdate();
                mMonitorKeyList.add(marketId);
                registerPeriodicUpdate(mMonitorKeyList);
                mCoinListAdapter.setItems(mMonitorKeyList);
                mCoinListAdapter.notifyDataSetChanged();
                Log.d(TAG, "[DEBUG] updateMonitorKey - update: "+marketId);
            }
        } else if (prevPrice != 0 && changedPrice / prevPrice < MONITOR_START_RATE * -2) {
            if (mMonitorKeyList.contains(marketId)) {
                removeMonitoringPeriodicUpdate();
                mMonitorKeyList.remove(marketId);
                registerPeriodicUpdate(mMonitorKeyList);
                mCoinListAdapter.setItems(mMonitorKeyList);
                mCoinListAdapter.notifyDataSetChanged();
                mTickerMapInfo.remove(marketId);
                mTradeMapInfo.remove(marketId);
                Log.d(TAG, "[DEBUG] updateMonitorKey - remove: "+marketId);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mActivity.getProcessor().removePeriodicUpdate(PERIODIC_UPDATE_TICKER_INFO_FOR_EVALUATION);
    }

    private void printLog() {
        Iterator<String> iterator = mTradeMapInfo.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Log.d(TAG, "[DEBUG] MARKET: "+mTradeMapInfo.get(key).getMarketId()
                    + " DATE: "+mTradeMapInfo.get(key).getTradeDateUtc()
                    + " TIME: "+mTradeMapInfo.get(key).getTradeTimeUtc()
                    + " timeStamp: "+mTradeMapInfo.get(key).getTimestamp()
                    + " Price: "+mTradeMapInfo.get(key).getTradePrice()
                    + " Volume: "+mTradeMapInfo.get(key).getTradeVolume().doubleValue()
                    + " PrevClosingPrice: "+mTradeMapInfo.get(key).getPrevClosingPrice()
                    + " RATE: "+mTradeMapInfo.get(key).getChangePrice()
                    + " ASK/BID: "+mTradeMapInfo.get(key).getAskBid()
                    + " SequentialId: "+mTradeMapInfo.get(key).getSequentialId()
                    + " RisingCount: "+mTradeMapInfo.get(key).getTickCount()
            );
        }
    }

    private TradeInfo getEvaluationTradeInfo(List<TradeInfo> tradesInfo) {
        DateFormat format = new SimpleDateFormat("HH:mm:ss");

        if (tradesInfo != null) {
            TradeInfo result= new TradeInfo();
            Iterator<TradeInfo> iterator = tradesInfo.iterator();

            int i = 0;
            while (iterator.hasNext()) {
                TradeInfo tradeInfo = iterator.next();
                TradeInfo prevTradeInfo = mTradeMapInfo.get(tradeInfo.getMarketId());
                if (prevTradeInfo == null) {
                    Log.d(TAG, "[DEBUG] getEvaluationTradeInfo first - getSequentialId: " + tradeInfo.getSequentialId() + " time: "+format.format(tradeInfo.getTimestamp()));

                    return tradeInfo;
                }
                if (prevTradeInfo != null && tradeInfo.getSequentialId() > prevTradeInfo.getSequentialId()) {
                    if (i == 0) {
                        result = tradeInfo;
                        result.setTickCount(prevTradeInfo.getTickCount());
                        result.setStartTime(prevTradeInfo.getStartTime());
                        if (prevTradeInfo.getRisingCount() > 0) {
                            result.setMonitoringStartTime(prevTradeInfo.getMonitoringStartTime());
                        }
                        result.setRisingCount(prevTradeInfo.getRisingCount());
                        result.setEndTime(tradeInfo.getEndTime());
                        result.setSequentialId(tradeInfo.getSequentialId());
                    }

                    if (result.getTickCount() == MONITOR_TICK_COUNTS - 1) {
                        result.setTickCount(0);
//                        if (result.getRisingCount() == 1) {
//                            result.setMonitoringStartTime(result.getMonitoringStartTime());
//                        }
                        result.setStartTime(tradeInfo.getTimestamp());

                        if (result.getEndTime() - result.getStartTime() > 3 * 1000) {
                            result.setRisingCount(0);
                        } else {
                            if (result.getEndTime() - result.getMonitoringStartTime() < 100 * 1000) {
                                double changedPrice = result.getTradePrice().floatValue() - prevTradeInfo.getTradePrice().floatValue();
                                double prevPrice = prevTradeInfo.getTradePrice().floatValue();
                                if (changedPrice / prevPrice > MONITOR_START_RATE) {
                                    if (result.getRisingCount() == 0) {
                                        result.setMonitoringStartTime(result.getMonitoringStartTime());
                                    }
                                    result.setRisingCount(tradeInfo.getRisingCount() + 1);
                                } else {
                                    result.setRisingCount(tradeInfo.getRisingCount() - 1);
                                }
                            }
                        }
                    }
                    result.setTickCount(result.getTickCount() + 1);

                    Log.d(TAG, "[DEBUG] getEvaluationTradeInfo - "
                                    + " getMarketId: " + result.getMarketId()
                                    + " getSequentialId: " + result.getSequentialId()
                                    + " time: "+format.format(result.getTimestamp())
                                    +" getRisingCount: "+result.getRisingCount()
                                    +" tickCount: "+result.getTickCount()
                                    +" getStartTime: "+result.getStartTime()
                                    +" getEndTime: "+result.getEndTime()
                                    +" getMonitoringStartTime: "+result.getMonitoringStartTime()
                    );
                    i++;
                }
            }
            return result;
        }
        return null;
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
        mActivity.getProcessor().removePeriodicUpdate(PERIODIC_UPDATE_TICKER_INFO_FOR_EVALUATION);
        mActivity.getProcessor().removePeriodicUpdate(UPDATE_TRADE_INFO_FOR_COIN_EVALUATION);
    }

    private void updateKeySet(List<String> keySet, MonthCandle monthCandle) {
        Iterator<String> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (key.equals(monthCandle.getMarketId())) {
                Double tradeAmount = (monthCandle.getCandleAccTradePrice().doubleValue() / 100000000);
                if (tradeAmount < 10000 && monthCandle.getTradePrice().intValue() < 100) {
                    iterator.remove();
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
                return;
            }
            holder.mCurrentPrice.setText(mNonZeroFormat.format(ticker.getTradePrice().intValue()));
        }

        @Override
        public int getItemCount() {
            return mCoinListInfo != null ? mCoinListInfo.size() : 0;
        }
    }
}
