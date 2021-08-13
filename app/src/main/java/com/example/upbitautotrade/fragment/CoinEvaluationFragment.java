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
import com.example.upbitautotrade.utils.BuyingItem;
import com.example.upbitautotrade.viewmodel.CoinEvaluationViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TimeZone;

import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_MARKETS_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_TICKER_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_MIN_CANDLE_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_TRADE_INFO;

public class CoinEvaluationFragment extends Fragment {
    public static final String TAG = "CoinEvaluationFragment";

    public final String MARKET_NAME = "KRW";
    public final String MARKET_WARNING = "CAUTION";

    private final double MONITORING_THRESHOLD_RATE = 0.03;

    private final int TRADE_COUNTS = 60;
    private final int TICK_TURNS = 6;
    private final double MONITOR_RISING_POINT = 1;
    private final double MONITOR_POINT_RATE = 0.001;
    private final int MONITOR_MIN_CANDLE_COUNT = 5;
    private final double EVALUATION_TIME = 120  * 1000;
    private long EVALUATION_OFFSET_TIME = 60;

    private View mView;


    private UpBitTradeActivity mActivity;
    private CoinEvaluationViewModel mViewModel;

    private CoinListAdapter mCoinListAdapter;
    private CoinListAdapter mBuyingListAdapter;
    private List<String> mMonitorKeyList;
    private List<String> mBuyingItemKeyList;
    private ArrayList mDeadMarketList;
    private Map<String, MarketInfo> mMarketsMapInfo;
    private Map<String, Ticker> mTickerMapInfo;
    private Map<String, Candle> mMinCandleMapInfo;
    private Map<String, DayCandle> mDayCandleMapInfo;
    private Map<String, WeekCandle> mWeekCandleMapInfo;
    private Map<String, MonthCandle> mMonthCandleMapInfo;
    private Map<String, BuyingItem> mBuyingItemMapInfo;
    private Map<String, BuyingItem> mCandidateItemMapInfo;
    private Map<String, TradeInfo> mTradeMapInfo;

    boolean mIsStarting = false;
    boolean mIsActive = false;

    private String[] deadMarket = {
            "KRW-GLM", "KRW-WAX", "KRW-STR", "KRW-STM", "KRW-STE", "KRW-ARD", "KRW-MVL", "KRW-ORB", "KRW-HIV", "KRW-STR",
            "KRW-POL", "KRW-IQ ", "KRW-ELF", "KRW-DKA", "KRW-JST", "KRW-MTL", "KRW-QKC", "KRW-BOR", "KRW-SSX", "KRW-POW",
            "KRW-CRE", "KRW-TT ", "KRW-SBD", "KRW-GRS", "KRW-STP", "KRW-RFR", "KRW-HUM", "KRW-AER", "KRW-MBL", "KRW-MOC",
            "KRW-HUN", "KRW-AHT", "KRW-FCT", "KRW-TON", "KRW-CBK", "KRW-PLA", "KRW-BTG", "KRW-SC ", "KRW-ICX", "KRW-ANK",
            "KRW-IOS", "KRW-LSK", "KRW-KNC", "KRW-PUN", "KRW-STO"
    };

    public CoinEvaluationFragment() {
        mMarketsMapInfo = new HashMap<>();
        mTickerMapInfo = new HashMap<>();
        mMinCandleMapInfo = new HashMap<>();
        mDayCandleMapInfo = new HashMap<>();
        mWeekCandleMapInfo = new HashMap<>();
        mMonthCandleMapInfo = new HashMap<>();
        mDeadMarketList = new ArrayList(Arrays.asList(deadMarket));
        mMonitorKeyList = new ArrayList<>();
        mBuyingItemMapInfo = new HashMap<>();
        mBuyingItemKeyList = new ArrayList<>();
        mCandidateItemMapInfo = new HashMap<>();

        mTradeMapInfo = new HashMap<>();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        if (mViewModel != null) {
            mViewModel.getMarketsInfo().observe(
                    getViewLifecycleOwner(),
                    marketsInfo -> {
                        if (!mIsActive) {
                            return;
                        }
                        mMarketsMapInfo.clear();
                        Iterator<MarketInfo> iterator = marketsInfo.iterator();
                        while (iterator.hasNext()) {
                            MarketInfo marketInfo = iterator.next();
                            if (marketInfo.getMarketId().contains(MARKET_NAME+"-")
                                    && !marketInfo.getMarket_warning().contains(MARKET_WARNING)) {
                                if (mDeadMarketList.contains(marketInfo.getMarketId())) {
                                    continue;
                                }
                                mMarketsMapInfo.put(marketInfo.getMarketId(), marketInfo);
                            }
                        }
                        registerPeriodicUpdate(mMarketsMapInfo.keySet());
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
                            String key = tick.getMarketId();
                            mTickerMapInfo.put(key, tick);
                            buyingSimulation(key, tick);
                        }
                        mCoinListAdapter.setMonitoringItems(mMonitorKeyList);
                        mCoinListAdapter.notifyDataSetChanged();
                        mBuyingListAdapter.notifyDataSetChanged();
                    }
            );

            mViewModel.getMinCandleInfo().observe(
                    getViewLifecycleOwner(),
                    minCandles -> {
                        if (!mIsActive) {
                            return;
                        }
                        updateMonitorKey(minCandles);
                    }
            );

            mViewModel.getMonthCandleInfo().observe(
                    getViewLifecycleOwner(),
                    monthCandle -> {
                        if (!mIsActive) {
                            return;
                        }
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
                        if (!mIsActive) {
                            return;
                        }
                        makeTradeMapInfo(tradesInfo);
                    }
            );
        }
    }

    private void updateMonitorKey(List<Candle> minCandlesInfo) {
        if (minCandlesInfo == null || minCandlesInfo.isEmpty()) {
            return;
        }
        double[] tradePrice = new double[MONITOR_MIN_CANDLE_COUNT];
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

        double firstPrice = tradePrice[0];
        double secondPrice = tradePrice[0];
        double prevPrice = tradePrice[MONITOR_MIN_CANDLE_COUNT - 1];

        double removeRate = secondPrice != 0 ? ((firstPrice - secondPrice) / secondPrice) : 0;
        double changedPrice = firstPrice - prevPrice;
        double rate = prevPrice != 0 ? (changedPrice / prevPrice) : 0;

        mMinCandleMapInfo.get(key).setChangedPrice(changedPrice);
        mMinCandleMapInfo.get(key).setChangedRate(rate);

        if (prevPrice != 0 && rate >= MONITORING_THRESHOLD_RATE) {
            if (!mMonitorKeyList.contains(key)) {
                removeMonitoringPeriodicUpdate();
                mMonitorKeyList.add(key);
                registerPeriodicUpdate(mMonitorKeyList);
                mCoinListAdapter.setMonitoringItems(mMonitorKeyList);
                mCoinListAdapter.notifyDataSetChanged();
            }
        } else if (prevPrice != 0 && removeRate < MONITORING_THRESHOLD_RATE * -0.5) {
            if (mBuyingItemKeyList.contains(key)) {
                sellingSimulation(key);
            }
            if (!mBuyingItemKeyList.contains(key) && mMonitorKeyList.contains(key)) {
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

    private void makeTradeMapInfo(List<TradeInfo> tradesInfo) {
        if (tradesInfo == null || tradesInfo.isEmpty()) {
            return;
        }

        String key = tradesInfo.get(0).getMarketId();
        Stack<TradeInfo> tradeInfoStack = new Stack<>();

        TradeInfo prevTradeInfo = mTradeMapInfo.get(key);
        long prevTradeInfoSeqId = prevTradeInfo != null ? prevTradeInfo.getSequentialId() : 0;
        int tickCount = prevTradeInfo != null ? prevTradeInfo.getTickCount() : 0;
        int tickTurn = prevTradeInfo != null ? prevTradeInfo.getTickTurn() : 0;
        double minPrice = prevTradeInfo != null ? prevTradeInfo.getMinPrice() : 0;
        double tradePrice = prevTradeInfo != null ? prevTradeInfo.getTradePrice().doubleValue() : 0;
        long startTime = prevTradeInfo != null ? prevTradeInfo.getStartTime() : 0;
        long endTime = prevTradeInfo != null ? prevTradeInfo.getEndTime() : 0;
        long startTimeFirst = prevTradeInfo != null ? prevTradeInfo.getEvaluationStartTimeFirst() : 0;
        int risingPoint = prevTradeInfo != null ? prevTradeInfo.getRisingPoint() : 0;

        Iterator<TradeInfo> iterator = tradesInfo.iterator();
        while (iterator.hasNext()) {
            TradeInfo tradeInfo = iterator.next();
            key = tradeInfo.getMarketId();
            if (prevTradeInfoSeqId == 0) {
                tradeInfoStack.push(tradeInfo);
            } else if (tradeInfo.getSequentialId() > prevTradeInfoSeqId) {
                tradeInfoStack.push(tradeInfo);
            }
        }

        Deque<TradeInfo> dequeTradeInfo = new LinkedList<>();
        while (!tradeInfoStack.isEmpty()) {
            TradeInfo pop = tradeInfoStack.pop();
            tickCount++;

            long time = pop.getTimestamp();
            if (tickCount == 1) {
                startTime = time;
            }
            endTime = time;


            double currentPrice = pop.getTradePrice().doubleValue();
            if (minPrice == 0 || currentPrice <= minPrice) {
                minPrice = currentPrice;
            }

            double changedPrice = currentPrice - tradePrice;
            double rate = tradePrice != 0 ? changedPrice / tradePrice : 0;
            int point = (int) (rate / MONITOR_POINT_RATE);

//            Log.d(TAG, "[DEBUG] makeTradeMapInfo - "
//                    + " currentPrice: " + currentPrice
//                    + " tradePrice: " + tradePrice
//                    + " changedPrice: " + changedPrice
//                    + " rate: " + rate
//                    + " point: " + point
//            );
            tradePrice = currentPrice;

            if (rate >= 0) {
                if (risingPoint == 0) {
                    startTimeFirst = time;
                }
                risingPoint = risingPoint + point;
            } else {
                if (risingPoint + point < 0) {
                    risingPoint = 0;
                    startTimeFirst = 0;
                } else {
                    risingPoint = risingPoint + point;
                }
            }


            pop.setTickCount(tickCount);
            pop.setTickTurn(tickTurn);
            pop.setMinPrice(minPrice);
            pop.setStartTime(startTime);
            pop.setEndTime(endTime);
            pop.setEvaluationStartTimeFirst(startTimeFirst);
            pop.setRisingPoint(risingPoint);

            dequeTradeInfo.offer(pop);
        }

        TradeInfo newTradeInfo = null;
        if (dequeTradeInfo != null) {
            DateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
            format.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

            while (!dequeTradeInfo.isEmpty() && dequeTradeInfo.peek().getTickCount() <= TRADE_COUNTS) {
                newTradeInfo = dequeTradeInfo.poll();
                if (newTradeInfo.getTickCount() == TRADE_COUNTS) {
                    newTradeInfo.setTickCount(0);
                    newTradeInfo.setTickTurn(tickTurn + 1);
                }
                Log.d(TAG, "[DEBUG] makeTradeMapInfo - "
                        + " getMarketId: " + newTradeInfo.getMarketId()
                        + " getSequentialId: " + newTradeInfo.getSequentialId()
                        + " getMinPrice: " + newTradeInfo.getMinPrice()
                        + " time: " + format.format(newTradeInfo.getTimestamp())
                        + " getRisingPoint: " + newTradeInfo.getRisingPoint()
                        + " tickCount: " + newTradeInfo.getTickCount()
                        + " TickTurn: " + newTradeInfo.getTickTurn()
                        + " getStartTime: " + format.format(newTradeInfo.getStartTime())
                        + " getEndTime: " + format.format(newTradeInfo.getEndTime())
                        + " (EndTime -StartTime): " + (newTradeInfo.getEndTime() - newTradeInfo.getStartTime())
                        + " getMonitoringStartTime: " + format.format(newTradeInfo.getEvaluationStartTimeFirst())
                );
            }
        }


        if (newTradeInfo != null) {
            long endToStart = newTradeInfo.getEndTime() - newTradeInfo.getStartTime();
            long endToStartFirst = newTradeInfo.getEndTime() - newTradeInfo.getEvaluationStartTimeFirst();
            long tickNumber = newTradeInfo.getTickCount();
            long tickTurnCounts = newTradeInfo.getTickTurn();
            long risePoint = newTradeInfo.getRisingPoint();

            if (tickNumber == 0) {
                DateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
                format.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                Log.d(TAG, "[DEBUG] makeTradeMapInfo - checking to evaluationToBuy "
                        + " getMarketId: " + newTradeInfo.getMarketId()
                        + " endToStart: " + endToStart
                        + " endToStartFirst: " + endToStartFirst
                        + " tickNumber: " + tickNumber
                        + " tickTurnCounts: " + tickTurnCounts
                        + " risePoint: " + risePoint
                        + " getStartTime: " + format.format(newTradeInfo.getStartTime())
                        + " getEndTime: " + format.format(newTradeInfo.getEndTime())
                        + " getEvaluationStartTimeFirst: " + format.format(newTradeInfo.getEvaluationStartTimeFirst())
                        + " mBuyingItemKeyList.contains(key): " + mBuyingItemKeyList.contains(key)
                );
            }
            if (endToStart > EVALUATION_TIME
                    || endToStartFirst > EVALUATION_TIME * 5) {
                Log.d(TAG, "[DEBUG] makeTradeMapInfo: clear TradeInfo");
                newTradeInfo.setEvaluationStartTimeFirst(0);
                newTradeInfo.setMinPrice(0);
                newTradeInfo.setStartTime(0);
                newTradeInfo.setEndTime(0);
                newTradeInfo.setTickCount(0);
                newTradeInfo.setTickTurn(0);
                newTradeInfo.setRisingPoint(0);
            } else if (tickNumber == 0) {
                if (!mBuyingItemKeyList.contains(key)) {
                    if (endToStart <= EVALUATION_TIME
                            && tickTurnCounts >= TICK_TURNS
                            && risePoint >= MONITOR_RISING_POINT) {
                        Log.d(TAG, "[DEBUG] makeTradeMapInfo: add TradeInfo at Candidate");
                        evaluationToBuy(key, newTradeInfo);
                    }
                } else {
//                    double buyPrice = mBuyingItemMapInfo.get(key).getBuyingPrice();
//                    double currPrice = newTradeInfo.getTradePrice().doubleValue();
                }
            }
        }

        if (newTradeInfo != null) {
            mTradeMapInfo.put(key, newTradeInfo);
        }
    }

    private void evaluationToBuy(String key, TradeInfo newTradeInfo) {

        double changed = Math.abs(newTradeInfo.getTradePrice().doubleValue() - newTradeInfo.getMinPrice());
        long evalTime = (newTradeInfo.getEndTime() - newTradeInfo.getEvaluationStartTimeFirst()) / 1000;
        double offsetPrice = (changed - (changed * (evalTime - EVALUATION_OFFSET_TIME) / (evalTime + EVALUATION_OFFSET_TIME)));

        double tradePrice = newTradeInfo.getTradePrice().doubleValue() - offsetPrice;

        DateFormat format = new SimpleDateFormat("HH:mm:ss.sss", Locale.KOREA);
        format.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        Log.d(TAG, "[DEBUG] Candidate to BUY - market: " + newTradeInfo.getMarketId()
                + " BuyPrice: " + tradePrice
                + " changed: " + changed
                + " evalTime: " + evalTime
                + " getEndTime: " + format.format(newTradeInfo.getEndTime())
                + " getEvaluationStartTimeFirst: " + format.format(newTradeInfo.getEvaluationStartTimeFirst())
                + " offsetPrice: " + offsetPrice
        );


        BuyingItem item = new BuyingItem();
        item.setMarketId(key);
        item.setBuyingPrice(tradePrice);
        item.setEndTime(newTradeInfo.getEndTime());
        item.setStartTimeFirst(newTradeInfo.getEvaluationStartTimeFirst());
        item.setStatus(item.WAITING);

        mBuyingItemKeyList.add(key);
        mBuyingItemMapInfo.put(key, item);
        mBuyingListAdapter.setBuyingItems(mBuyingItemKeyList);
        mBuyingListAdapter.notifyDataSetChanged();
    }

    private void buyingSimulation(String key, Ticker ticker) {
        BuyingItem item = mBuyingItemMapInfo.get(key);
        if (item != null && item.getBuyingPrice() >= ticker.getTradePrice().doubleValue()) {

            if (mBuyingItemKeyList.contains(key) && item.getStatus() == item.WAITING) {
                Log.d(TAG, "[DEBUG] BUY - !!!! : " +key);
                if (item.getEndTime() - item.getStartTimeFirst() < EVALUATION_TIME * 5) {
                    item.setBuyingTime(ticker.getTimestamp());
                    item.setStatus(item.BUY);
                } else if (item.getEndTime() - item.getStartTimeFirst() > EVALUATION_TIME * 5 * 1000) {
                    mBuyingItemKeyList.remove(key);
                    mBuyingItemMapInfo.remove(key);
                }
                mBuyingListAdapter.notifyDataSetChanged();
            }
        }
    }

    private void sellingSimulation(String key) {
        if (mBuyingItemMapInfo != null && mBuyingItemMapInfo.containsKey(key)) {
            Log.d(TAG, "[DEBUG] SELL - !!!! : " +key);
            BuyingItem item = mBuyingItemMapInfo.get(key);
            item.setStatus(item.SELL);
            mBuyingListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mIsActive = false;
        if (!mIsStarting) {
            mActivity.getProcessor().stopBackgroundProcessor();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.getProcessor().setViewModel(mActivity.getCoinEvaluationViewModel(), mActivity.getAccessKey(), mActivity.getSecretKey());
        mViewModel =  mActivity.getCoinEvaluationViewModel();
        mIsActive = true;
        mActivity.getProcessor().startBackgroundProcessor();
        mActivity.getProcessor().registerProcess(UPDATE_MARKETS_INFO, null);
    }

    private void registerPeriodicUpdate(Set<String> keySet) {
        Iterator<String> regIterator = keySet.iterator();
        while (regIterator.hasNext()) {
            String key = regIterator.next();
            if (!key.equals("KRW-KRW")) {
                mActivity.getProcessor().registerPeriodicUpdate(UPDATE_MIN_CANDLE_INFO, key, MONITOR_MIN_CANDLE_COUNT, 1);
            }
        }
    }

    private void registerPeriodicUpdate(List<String> monitorKeyList) {
        Iterator<String> monitorIterator = monitorKeyList.iterator();
        while (monitorIterator.hasNext()) {
            String key = monitorIterator.next();
            if (!key.equals("KRW-KRW")) {
                mActivity.getProcessor().registerPeriodicUpdate(UPDATE_TICKER_INFO, key);
                mActivity.getProcessor().registerPeriodicUpdate(UPDATE_TRADE_INFO, key, TRADE_COUNTS);
            }
        }
    }

    private void removeMonitoringPeriodicUpdate() {
        mActivity.getProcessor().removePeriodicUpdate(UPDATE_TICKER_INFO);
        mActivity.getProcessor().removePeriodicUpdate(UPDATE_TRADE_INFO);
    }

    private class CoinHolder extends RecyclerView.ViewHolder {
        public TextView mCoinName;
        public TextView mCoinStatus;
        public TextView mCurrentPrice;
        public TextView mRatePerMin;
        public TextView mRate;
        public TextView mTickAmount;
        public TextView mAmountPerMin;
        public TextView mChangeRate;
        public TextView mBuyingPrice;
        public TextView mBuyingTime;

        public CoinHolder(@NonNull @NotNull View itemView, boolean isMonitor) {
            super(itemView);
            if (isMonitor) {
                mCoinName = itemView.findViewById(R.id.coin_name);
                mCurrentPrice = itemView.findViewById(R.id.coin_current_price);
                mRate = itemView.findViewById(R.id.coin_change_rate);
                mRatePerMin = itemView.findViewById(R.id.coin_1min_change_rate);
                mTickAmount = itemView.findViewById(R.id.buying_price);
                mAmountPerMin = itemView.findViewById(R.id.buy_time);
            } else {
                mCoinName = itemView.findViewById(R.id.coin_name);
                mCoinStatus = itemView.findViewById(R.id.coin_status);
                mCurrentPrice = itemView.findViewById(R.id.coin_current_price);
                mChangeRate = itemView.findViewById(R.id.coin_1min_change_rate);
                mBuyingPrice = itemView.findViewById(R.id.buying_price);
                mBuyingTime = itemView.findViewById(R.id.buy_time);
            }
        }
    }

    private class CoinListAdapter extends RecyclerView.Adapter<CoinHolder> {
        private DecimalFormat mFormat;
        private DecimalFormat mNonZeroFormat;
        private DecimalFormat mPercentFormat;
        private SimpleDateFormat mTimeFormat;
        private List<String> mCoinListInfo;
        private List<String> mBuyingListInfo;
        private boolean mIsMonitor;

        public CoinListAdapter(boolean isMonitor) {
            mIsMonitor = isMonitor;
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
                    holder.mRate.setText(mPercentFormat.format(ticker.getSignedChangeRate()));
                    holder.mTickAmount.setText(mFormat.format(ticker.getAccTradePrice24h().doubleValue() / 10000000));
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
                if (buyingItem != null
                        && (buyingItem.getStatus().equals(buyingItem.WAITING)
                        || buyingItem.getStatus().equals(buyingItem.BUY))) {
                    holder.mBuyingPrice.setText(mNonZeroFormat.format(buyingItem.getBuyingPrice()));
                    double changedPrice = currentPrice - buyingItem.getBuyingPrice();
                    double prevPrice = buyingItem.getBuyingPrice();
                    double rate = prevPrice != 0 ? (changedPrice / (double) prevPrice) : 0;
                    holder.mCoinStatus.setText(buyingItem.getStatus());
                    holder.mChangeRate.setText(mPercentFormat.format(rate));
                    holder.mBuyingTime.setText(mTimeFormat.format(buyingItem.getBuyingTime()));
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
