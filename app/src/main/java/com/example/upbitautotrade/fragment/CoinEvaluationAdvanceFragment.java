package com.example.upbitautotrade.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upbitautotrade.R;
import com.example.upbitautotrade.appinterface.UpBitTradeActivity;
import com.example.upbitautotrade.model.CoinInfo;
import com.example.upbitautotrade.model.DayCandle;
import com.example.upbitautotrade.model.MarketInfo;
import com.example.upbitautotrade.model.Post;
import com.example.upbitautotrade.model.ResponseOrder;
import com.example.upbitautotrade.model.Ticker;
import com.example.upbitautotrade.model.TradeInfo;
import com.example.upbitautotrade.utils.NumberWatcher;
import com.example.upbitautotrade.viewmodel.CoinEvaluationViewModel;
import com.example.upbitautotrade.viewmodel.UpBitViewModel;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import java.util.UUID;

import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_DAY_CANDLE_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_DELETE_ORDER_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_MARKETS_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_POST_ORDER_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_SEARCH_ORDER_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_TICKER_INFO;
import static com.example.upbitautotrade.utils.BackgroundProcessor.UPDATE_TRADE_INFO;

public class CoinEvaluationAdvanceFragment extends Fragment {
    public static final String TAG = "CoinEvaluationFragment";

    public final String MARKET_NAME = "KRW";
    public final String MARKET_WARNING = "CAUTION";
    private final long RESET_TIMER = 20 * 60 * 1000;
    private final long RESET_TIMER_GAP = 1 * 60 * 1000;
    private final int COIN_LIST_MAX_NUM = 30;

    private int mCoinListNum = COIN_LIST_MAX_NUM;
    private final double PRICE_AMOUNT = 10000;
    private final double MONITORING_PERIOD_TIME = 3;
    private final int TICK_COUNTS = 1500;
    private final double TRADE_RATE = 0.045;
    private final int TRADE_COUNTS = TICK_COUNTS;

    private View mView;


    private UpBitTradeActivity mActivity;
    private CoinEvaluationViewModel mViewModel;

    private ArrayList mDeadMarketList;
    private Map<String, MarketInfo> mMarketsMapInfo;
    private Map<String, DayCandle> mDayCandleMapInfo;
    private List<String> mCoinItemList;
    private ProgressDialog mProgressDialog;
    private long mLastResetTime = 0;

    // Result View
    private CoinListAdapter mResultListAdapter;
    private List<CoinInfo> mResultListInfo;
    private Map<String, ResponseOrder> mResponseOrderInfoMap;

    // Buying View
    private CoinListAdapter mBuyingListAdapter;
    private Map<String, CoinInfo> mBuyingItemMapInfo;
    private List<String> mBuyingItemKeyList;
    private Map<String, Ticker> mTickerMapInfo;

    // Monitoring View
    private CoinListAdapter mCoinListAdapter;
    private Map<String, Deque<TradeInfo>> mTradeMapInfo;
    private List<String> mMonitorKeyList;

    // Parameter
    private double mPriceAmount = PRICE_AMOUNT;
    private double mMonitorTime = MONITORING_PERIOD_TIME * (60 * 1000);
    private double mMonitorRate = TRADE_RATE;
    private double mMonitorTick = TICK_COUNTS;

    boolean mIsStarting = false;
    boolean mIsActive = false;
    boolean mIsShortMoney = false;

    private DecimalFormat mZeroFormat;
    private DecimalFormat mExtendZeroFormat;
    private DecimalFormat mNonZeroFormat;
    private DecimalFormat mPercentFormat;
    private SimpleDateFormat mTimeFormat;

    private String[] deadMarket = {
            "KRW-GLM", "KRW-WAX", "KRW-STR", "KRW-STM", "KRW-STE", "KRW-ARD", "KRW-MVL", "KRW-ORB", "KRW-HIV", "KRW-STR",
            "KRW-POL", "KRW-IQ ", "KRW-ELF", "KRW-DKA", "KRW-JST", "KRW-MTL", "KRW-QKC", "KRW-BOR", "KRW-SSX", "KRW-POW",
            "KRW-CRE", "KRW-TT ", "KRW-SBD", "KRW-GRS", "KRW-STP", "KRW-RFR", "KRW-HUM", "KRW-AER", "KRW-MBL", "KRW-MOC",
            "KRW-HUN", "KRW-AHT", "KRW-FCT", "KRW-TON", "KRW-CBK", "KRW-PLA", "KRW-BTG", "KRW-SC ", "KRW-ICX", "KRW-ANK",
            "KRW-IOS", "KRW-LSK", "KRW-KNC", "KRW-PUN", "KRW-STO"
    };
    private boolean mForceReset = false;
    private boolean mPreventReset = false;

    public CoinEvaluationAdvanceFragment() {
        mDeadMarketList = new ArrayList(Arrays.asList(deadMarket));
        mMarketsMapInfo = new HashMap<>();
        mDayCandleMapInfo = new HashMap<>();
        mCoinItemList = new ArrayList<>();

        mResultListInfo = new ArrayList<>();
        mResponseOrderInfoMap = new HashMap<>();

        mBuyingItemMapInfo = new HashMap<>();
        mBuyingItemKeyList = new ArrayList<>();
        mTickerMapInfo = new HashMap<>();

        mMonitorKeyList = new ArrayList<>();
        mTradeMapInfo = new HashMap<>();

        mZeroFormat = new DecimalFormat("###,###,###,###.##");
        mExtendZeroFormat = new DecimalFormat("###,###,###,###.###");
        mNonZeroFormat = new DecimalFormat("###,###,###,###");
        mPercentFormat = new DecimalFormat("###.##" + "%");
        mTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
        mTimeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (UpBitTradeActivity)getActivity();
        mViewModel =  mActivity.getCoinEvaluationViewModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_coin_evaluation_advance, container, false);
        RecyclerView resultList = mView.findViewById(R.id.coin_result_list);
        RecyclerView coinList = mView.findViewById(R.id.coin_evaluation_list);
        RecyclerView buyingList = mView.findViewById(R.id.coin_buying_list);

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);

        LinearLayoutManager layoutResultManager = new LinearLayoutManager(getContext());
        resultList.setLayoutManager(layoutResultManager);
        mResultListAdapter = new CoinListAdapter(mBuyingListAdapter.MODE_RESULT);
        resultList.setAdapter(mResultListAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        coinList.setLayoutManager(layoutManager);
        mCoinListAdapter = new CoinListAdapter(mBuyingListAdapter.MODE_MONITOR);
        coinList.setAdapter(mCoinListAdapter);

        LinearLayoutManager layoutBuyManager = new LinearLayoutManager(getContext());
        buyingList.setLayoutManager(layoutBuyManager);
        mBuyingListAdapter = new CoinListAdapter(mBuyingListAdapter.MODE_WAITING_OR_BUY);
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

        TextView buyingPriceText = mView.findViewById(R.id.trade_buying_price);
        TextView monitorTimeText = mView.findViewById(R.id.trade_monitor_time);
        TextView monitorRateText = mView.findViewById(R.id.trade_monitor_rate);
        TextView monitorTickText = mView.findViewById(R.id.trade_monitor_tick);
        EditText buyingPriceEditText = mView.findViewById(R.id.trade_input_buying_price);
        EditText monitorTimeEditText = mView.findViewById(R.id.trade_input_monitor_time);
        EditText monitorRateEditText = mView.findViewById(R.id.trade_input_monitor_rate);
        EditText monitorTickEditText = mView.findViewById(R.id.trade_input_monitor_tick);

        buyingPriceEditText.addTextChangedListener(new NumberWatcher(buyingPriceEditText));

        DecimalFormat nonZeroFormat = new DecimalFormat("###,###,###,###");
        DecimalFormat zeroFormat = new DecimalFormat("###,###,###,###.#");
        DecimalFormat percentFormat = new DecimalFormat("###.##" + "%");

        buyingPriceText.setText(nonZeroFormat.format(PRICE_AMOUNT));
        monitorTimeText.setText(zeroFormat.format(MONITORING_PERIOD_TIME));
        monitorRateText.setText(percentFormat.format(TRADE_RATE));
        monitorTickText.setText(nonZeroFormat.format(TICK_COUNTS));
        buyingPriceEditText.setText(nonZeroFormat.format(PRICE_AMOUNT));
        monitorTimeEditText.setText(zeroFormat.format(MONITORING_PERIOD_TIME));
        monitorRateEditText.setText(Double.toString(TRADE_RATE * 100));
        monitorTickEditText.setText(nonZeroFormat.format(TICK_COUNTS));

        Button applyButton = mView.findViewById(R.id.trade_input_button);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsShortMoney = false;

                String buyingPrice = buyingPriceEditText.getText().toString();
                String monitorTime = monitorTimeEditText.getText().toString();
                String monitorRate = monitorRateEditText.getText().toString();
                String monitorTick = monitorTickEditText.getText().toString();

                try {
                    mPriceAmount = (buyingPrice != null || !buyingPrice.isEmpty()) ? Double.parseDouble(buyingPrice.replace(",","")) : PRICE_AMOUNT;
                    mMonitorTime = (monitorTime != null || !monitorTime.isEmpty()) ? Double.parseDouble(monitorTime) * 60 * 1000 : MONITORING_PERIOD_TIME * 60 * 1000;
                    mMonitorRate = (monitorRate != null || !monitorRate.isEmpty()) ? Double.parseDouble(monitorRate.replace("%", "")) / 100 : TRADE_RATE;
                    mMonitorTick = (monitorTick != null || !monitorTick.isEmpty()) ? Double.parseDouble(monitorTick.replace(",","")) : TICK_COUNTS;
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Error NumberFormatException");
                }

                Log.d(TAG, "onClick -mPriceAmount: " + mZeroFormat.format(mPriceAmount)
                        + " mMonitorTime: " + mTimeFormat.format(mMonitorTime)
                        + " mMonitorRate: " + mPercentFormat.format(mMonitorRate)
                        + " mMonitorTick: " + mMonitorTick
                );

                buyingPriceText.setText(nonZeroFormat.format(mPriceAmount));
                monitorTimeText.setText(zeroFormat.format(mMonitorTime / (60 * 1000)));
                monitorRateText.setText(percentFormat.format(mMonitorRate));
                monitorTickText.setText(nonZeroFormat.format(mMonitorTick));


                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(buyingPriceEditText.getWindowToken(), 0);
                inputMethodManager.hideSoftInputFromWindow(monitorTimeEditText.getWindowToken(), 0);
                inputMethodManager.hideSoftInputFromWindow(monitorRateEditText.getWindowToken(), 0);
                inputMethodManager.hideSoftInputFromWindow(monitorTickEditText.getWindowToken(), 0);
            }
        });

        mViewModel.setOnPostErrorListener(new UpBitViewModel.RequestErrorListener() {
            @Override
            public void shortMoney(String uuid, String type) {
                if (type.equals("bid")) {
                    mIsShortMoney = true;
                    mPreventReset = false;

                    try {
                        Thread.sleep(15 * 1000);
                    } catch (InterruptedException e) {
                        Log.w(TAG, "shortMoney: bid");
                    }

                    Map<String, CoinInfo> buyingItem = new HashMap<>();
                    buyingItem.putAll(mBuyingItemMapInfo);
                    Iterator<String> delOrderIterator = buyingItem.keySet().iterator();
                    while (delOrderIterator.hasNext()) {
                        String key = delOrderIterator.next();
                        CoinInfo coinInfo = mBuyingItemMapInfo.get(key);
                        if (coinInfo.getStatus().equals(CoinInfo.READY)) {
                            Log.d(TAG, "[DEBUG] Reset Coin Item Delete READY - key : " + key + " deleteUUID: " + coinInfo.getUuid());
                            mBuyingItemMapInfo.remove(key);
                        }
                    }

                    Activity activity = getActivity();
                    if (activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "매수 금액이 부족합니다.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } else {
                    mIsShortMoney = false;
                }

                String key = null;
                Iterator<ResponseOrder> iterator = mResponseOrderInfoMap.values().iterator();
                while (iterator.hasNext()) {
                    ResponseOrder order = iterator.next();
                    if (order.getUuid().equals(uuid)) {
                        key = order.getMarket();
                        break;
                    }
                }

                if (key != null) {
                    CoinInfo coinInfo = mBuyingItemMapInfo.get(key);
                    if (coinInfo == null) {
                        return;
                    }
                    Log.d(TAG, "[DEBUG] shortMoney key : " + key +" mIsShortMoney: " + mIsShortMoney + " uuid: " + uuid);

                    coinInfo.setMarketId(key);
                    coinInfo.setStatus(CoinInfo.SELL);
                    coinInfo.setSellTime(System.currentTimeMillis());
                    Ticker ticker = mTickerMapInfo.get(key);
                    coinInfo.setSellPrice(ticker != null &&  ticker.getTradePrice() != null ? ticker.getTradePrice().doubleValue() : 0);

                    mResultListInfo.add(coinInfo);
                    mResultListAdapter.setResultItems(mResultListInfo);

                    removeMonitoringPeriodicUpdate(UPDATE_SEARCH_ORDER_INFO, key);
                    removeMonitoringPeriodicUpdate(UPDATE_TICKER_INFO, key);

                    mResponseOrderInfoMap.remove(key);
                    mBuyingItemKeyList.remove(key);
                    mBuyingItemMapInfo.remove(key);
                    mBuyingListAdapter.setBuyingItems(mBuyingItemKeyList);

                    updateProfitInfo();
                }
            }

            @Override
            public void deleteError(String uuid) {
                String key = null;
                Iterator<ResponseOrder> iterator = mResponseOrderInfoMap.values().iterator();
                while (iterator.hasNext()) {
                    ResponseOrder order = iterator.next();
                    if (order.getUuid().equals(uuid)) {
                        key = order.getMarket();
                        break;
                    }
                }

                if (key != null) {
                    Log.d(TAG, "[DEBUG] deleteError key : " + key +" uuid: " + uuid);
                    removeMonitoringPeriodicUpdate(UPDATE_SEARCH_ORDER_INFO, key);
                    removeMonitoringPeriodicUpdate(UPDATE_TICKER_INFO, key);

                    mResponseOrderInfoMap.remove(key);
                    mBuyingItemKeyList.remove(key);
                    mBuyingItemMapInfo.remove(key);
                    mBuyingListAdapter.setBuyingItems(mBuyingItemKeyList);
                }
            }
        });

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mViewModel != null) {
            DateFormat format = new SimpleDateFormat("HH:mm:ss.sss", Locale.KOREA);
            format.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

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
                                if (marketInfo.getMarketId().contains(MARKET_NAME+"-"+MARKET_NAME)) {
                                    continue;
                                }
                                mMarketsMapInfo.put(marketInfo.getMarketId(), marketInfo);
                            }
                        }
                        if (!mMarketsMapInfo.keySet().equals(mDayCandleMapInfo.keySet())) {
                            registerProcess(UPDATE_DAY_CANDLE_INFO, mMarketsMapInfo.keySet());
                            mProgressDialog.setMessage("Updating Coin List...");
                            mProgressDialog.show();
                        } else {
                            if (mCoinItemList != null && mCoinItemList.size() >= mCoinListNum) {
                                registerPeriodicUpdate(mCoinItemList);
                            }
                        }
                    }
            );

            mViewModel.getDayCandleInfo().observe(
                    getViewLifecycleOwner(),
                    weekCandles -> {
                        if (!mIsActive) {
                            return;
                        }
                        Iterator<DayCandle> iterator = weekCandles.iterator();
                        while (iterator.hasNext()) {
                            DayCandle candle = iterator.next();
                            mDayCandleMapInfo.put(candle.getMarketId(), candle);
                        }

                        if (mMarketsMapInfo.keySet().equals(mDayCandleMapInfo.keySet())) {
                            List<String> coinItemList = new ArrayList<>(mDayCandleMapInfo.keySet());
                            Collections.sort(coinItemList, (value1, value2) -> mDayCandleMapInfo.get(value1).compareTo(mDayCandleMapInfo.get(value2)));
//                            int listNum = 0;
//                            for (int i = 0; i < coinItemList.size(); i++) {
//                                String key = coinItemList.get(i);
//                                if (listNum >= COIN_LIST_MAX_NUM) {
//                                    continue;
//                                }
//                                if (key != null && mDayCandleMapInfo.get(key).getChangedVolumeRate() > 0 ) {
//                                    listNum++;
//                                }
//                                /* if (key != null && mDayCandleMapInfo.get(key).getChangedVolumeRate() > 0 ) {
//                                    listNum++;
//                                    mCoinItemList.add(key);
//                                    Log.d(TAG, "[DEBUG] getDayCandleInfo - getChangedVolumeRate key: " + key
//                                            + " rate: " + mDayCandleMapInfo.get(key).getChangeRate()
//                                            + " getChangedVolumeRate: " + mDayCandleMapInfo.get(key).getChangedVolumeRate()
//                                            + " amount: "+(mDayCandleMapInfo.get(key).getChangedVolumeRate() * mDayCandleMapInfo.get(key).getCandleAccTradePrice().doubleValue())
//                                    );
//                                } else if (key != null && mDayCandleMapInfo.get(key).getCenterChangedRate() >= 0 ){
//                                    listNum++;
//                                    mCoinItemList.add(key);
//                                    Log.d(TAG, "[DEBUG] getDayCandleInfo - getCenterChangedRate key: " + key
//                                            + " rate: " + mDayCandleMapInfo.get(key).getChangeRate()
//                                            + " getChangedVolumeRate: " + mDayCandleMapInfo.get(key).getChangedVolumeRate()
//                                            + " amount: "+(mDayCandleMapInfo.get(key).getChangedVolumeRate() * mDayCandleMapInfo.get(key).getCandleAccTradePrice().doubleValue())
//                                    );
//                                }*/
//                            }

//                            mCoinListNum = Math.min(COIN_LIST_MAX_NUM, listNum);
//                            Log.d(TAG, "COIN List NUM: " + mCoinListNum);
                            mProgressDialog.dismiss();
//                            if (listNum <= 0) {
//                                mCoinItemList.clear();
//                                mDayCandleMapInfo.clear();
//                                mActivity.getProcessor().registerProcess(UPDATE_MARKETS_INFO, null);
//                                return;
//                            }
                            mCoinItemList.addAll(coinItemList.subList(0, mCoinListNum));
                            registerPeriodicUpdate(mCoinItemList);
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
                            updateTradeMapInfoByTicker(key, tick);
                        }
                        mCoinListAdapter.setMonitoringItems(mMonitorKeyList);
                        mBuyingListAdapter.setBuyingItems(mBuyingItemKeyList);
                        mResultListAdapter.setResultItems(mResultListInfo);
                    }
            );

            mViewModel.getPostOrderInfo().observe(
                    getViewLifecycleOwner(),
                    orderInfo -> {
                        if (!mIsActive) {
                            return;
                        }
                        registerPeriodicUpdate(UPDATE_SEARCH_ORDER_INFO, orderInfo.getMarket(), orderInfo.getUuid());
                    }
            );

            mViewModel.getSearchOrderInfo().observe(
                    getViewLifecycleOwner(),
                    orderInfo -> {
                        if (!mIsActive) {
                            return;
                        }
                        updateResponseOrderInfo(orderInfo);
                    }
            );

            mViewModel.getDeleteOrderInfo().observe(
                    getViewLifecycleOwner(),
                    orderInfo -> {
                        if (!mIsActive) {
                            return;
                        }
                        deleteOrderInfo(orderInfo);
                    }
            );

        }
    }

    private void makeTradeMapInfo(List<TradeInfo> tradesInfo) {
        if (tradesInfo == null || tradesInfo.isEmpty()) {
            return;
        }

        String key = tradesInfo.get(0).getMarketId();
        Stack<TradeInfo> tradeInfoStack = new Stack<>();
        Iterator<TradeInfo> stackIterator = tradesInfo.iterator();
        while (stackIterator.hasNext()) {
            TradeInfo tradeInfo = stackIterator.next();
            tradeInfoStack.push(tradeInfo);
        }

        Deque<TradeInfo> prevTradeInfo = mTradeMapInfo.get(key);
        Deque<TradeInfo> tradeInfoQueue = prevTradeInfo != null ? prevTradeInfo : new LinkedList<>();
        long prevTradeInfoSeqId = prevTradeInfo != null ? prevTradeInfo.getLast().getSequentialId() : 0;
        long prevTradeInfoTime = prevTradeInfo != null ? prevTradeInfo.getLast().getTimestamp() : 0;

        while (!tradeInfoStack.isEmpty()) {
            TradeInfo tradeInfo= tradeInfoStack.pop();
            if (prevTradeInfoSeqId == 0) {
                tradeInfoQueue.offer(tradeInfo);
                break;
            } else if (tradeInfo.getSequentialId() > prevTradeInfoSeqId) {
                tradeInfoQueue.offer(tradeInfo);
            }
        }

        double lowPrice = 0;
        double highPrice = 0;
        Iterator<TradeInfo> removeIterator = tradeInfoQueue.iterator();
        while (removeIterator.hasNext()) {
            TradeInfo tradeInfo = removeIterator.next();
            if (tradeInfoQueue.peekLast().getTimestamp() - tradeInfo.getTimestamp() > mMonitorTime) {
                removeIterator.remove();
            } else {
                double price = tradeInfo.getTradePrice().doubleValue();
                lowPrice = lowPrice == 0 ? price : Math.min(lowPrice, price);
                highPrice = Math.max(highPrice, price);
            }
        }
        mTradeMapInfo.put(key, tradeInfoQueue);

        updateTradeMapInfoByTradeInfo(key, tradeInfoQueue, highPrice, lowPrice);
    }

    private void updateTradeMapInfoByTradeInfo(String key, Deque<TradeInfo> tradeInfoQueue, double highPrice, double lowPrice) {
        double openPrice = tradeInfoQueue.getFirst().getTradePrice().doubleValue();
        double closePrice = tradeInfoQueue.getLast().getTradePrice().doubleValue();
        double candleRate = openPrice != 0 ? (closePrice - openPrice) / openPrice : 0;
        candleRate = (double)Math.round(candleRate * 1000) / 1000;

        double highLowRate = lowPrice != 0 ? (highPrice - lowPrice) / lowPrice : 0;
        highLowRate = (double)Math.round(highLowRate * 1000) / 1000;

        double tickCount = tradeInfoQueue.size();

        if (mBuyingItemKeyList.contains(key)) {
            CoinInfo coinInfo = mBuyingItemMapInfo.get(key);
            if (coinInfo != null) {
                if (coinInfo.getStatus().equals(CoinInfo.BUY)) {
                    long duration = System.currentTimeMillis() - coinInfo.getBuyTime();
                    if (duration >= mMonitorTime) {
                        coinInfo.setMaxProfitRate(highPrice);
                    }
                }
                coinInfo.setMarketId(key);
                coinInfo.setOpenPrice(openPrice);
                coinInfo.setClosePrice(closePrice);
                coinInfo.setHighPrice(highPrice);
                coinInfo.setLowPrice(lowPrice);
                coinInfo.setTickCounts(tickCount);
                mBuyingItemMapInfo.put(key, coinInfo);
                mCoinListAdapter.setMonitoringItems(mMonitorKeyList);
                mBuyingListAdapter.setBuyingItems(mBuyingItemKeyList);
                mResultListAdapter.setResultItems(mResultListInfo);
                updateTradeMapInfoByTicker(key, null);

                Log.d(TAG, "updateTradeMapInfoByTradeInfo - key: " + key
                        + " high: " + mZeroFormat.format(coinInfo.getHighPrice())
                        + " close: " + mZeroFormat.format(coinInfo.getClosePrice())
                        + " open: " + mZeroFormat.format(coinInfo.getOpenPrice())
                        + " low: " + mZeroFormat.format(coinInfo.getLowPrice())
                        + " getBuyPrice: " + mZeroFormat.format(coinInfo.getBuyPrice())
                        + " priceChangedRate: " + mPercentFormat.format(candleRate)
                        + " highLowRate: " + mPercentFormat.format(highLowRate)
                        + " getMaxProfitRate: " + mPercentFormat.format(coinInfo.getMaxProfitRate())
                        + " getTickCounts: " + coinInfo.getTickCounts()
                );
                return;
            }
        } else {
            double upperTailGapPrice = highPrice - closePrice;
            double lowerTailGapPrice = openPrice - lowPrice;
            double bodyGapPrice = closePrice - openPrice;

            double upperTailBodyRate = bodyGapPrice != 0 ? upperTailGapPrice / bodyGapPrice : -1;
            upperTailBodyRate = (double)Math.round(upperTailBodyRate * 1000) / 1000;

            double lowerTailBodyRate = bodyGapPrice != 0 ? lowerTailGapPrice / bodyGapPrice : -1;
            lowerTailBodyRate = (double)Math.round(lowerTailBodyRate * 1000) / 1000;

            double upperLowerTailRate = lowerTailGapPrice != 0 ? (upperTailGapPrice - lowerTailGapPrice) / lowerTailGapPrice : -1;
            upperLowerTailRate = (double)Math.round(upperLowerTailRate * 1000) / 1000;

            Log.d(TAG, "[DEBUG] makeTradeMapInfo: - key: " + key
                    + " high: " + mZeroFormat.format(highPrice)
                    + " close: " + mZeroFormat.format(closePrice)
                    + " open: " + mZeroFormat.format(openPrice)
                    + " low: " + mZeroFormat.format(lowPrice)
                    + " candleRate: " + mPercentFormat.format(candleRate)
                    + " highLowRate: " + mPercentFormat.format(highLowRate)
                    + " upperTailGapPrice: " + mZeroFormat.format(upperTailGapPrice)
                    + " lowerTailGapPrice: " + mZeroFormat.format(lowerTailGapPrice)
                    + " bodyGapPrice: " + mZeroFormat.format(bodyGapPrice)
                    + " upperTailRate: " + mPercentFormat.format(upperTailBodyRate)
                    + " lowerTailRate: " + mPercentFormat.format(lowerTailBodyRate)
                    + " upperLowerTailRate: " + mPercentFormat.format(upperLowerTailRate)
                    + " getTickCounts: " + tickCount
            );

            if (tickCount >= mMonitorTick) {
                if (!mMonitorKeyList.contains(key)) {
                    mMonitorKeyList.add(key);
                }

                if (!mBuyingItemMapInfo.containsKey(key)) {
                    if (mIsShortMoney) {
                        Log.d(TAG, "updateTradeMapInfoByTradeInfo: not Enough money !!!");
                    } else if (candleRate >= mMonitorRate || highLowRate >= mMonitorRate) {
                        CoinInfo coinInfo = new CoinInfo(openPrice, closePrice, highPrice, lowPrice, tickCount);
                        // Post to Buy
                        tacticalToBuy(key, coinInfo);
                    }
                } else {
                    Log.d(TAG, "updateTradeMapInfoByTradeInfo - key : " + key + " skip tacticalToBuy() - duplicate  !!!");
                }
            } else {
                mMonitorKeyList.remove(key);
            }
            mCoinListAdapter.setMonitoringItems(mMonitorKeyList);
        }
    }

    private void tacticalToBuy(String key, CoinInfo coinInfo) {
        if (key == null || coinInfo == null) {
            return;
        }

        double openPrice = coinInfo.getOpenPrice();
        double closePrice = coinInfo.getClosePrice();
        double highPrice = coinInfo.getHighPrice();
        double lowPrice = coinInfo.getLowPrice();

        double upperTailGapPrice = highPrice - closePrice;
        double lowerTailGapPrice = openPrice - lowPrice;
        double bodyGapPrice = closePrice - openPrice;

        double upperTailBodyRate = bodyGapPrice != 0 ? upperTailGapPrice / bodyGapPrice : -1;
        upperTailBodyRate = (double)Math.round(upperTailBodyRate * 1000) / 1000;

        double lowerTailBodyRate = bodyGapPrice != 0 ? lowerTailGapPrice / bodyGapPrice : -1;
        lowerTailBodyRate = (double)Math.round(lowerTailBodyRate * 1000) / 1000;

        double upperLowerTailRate = lowerTailGapPrice != 0 ? (upperTailGapPrice - lowerTailGapPrice) / lowerTailGapPrice : -1;
        upperLowerTailRate = (double)Math.round(upperLowerTailRate * 1000) / 1000;

        double toBuyPrice = 0;
        double volume = 0;
        double candleRate = openPrice != 0 ? (closePrice - openPrice) / openPrice : 0;
        candleRate = (double)Math.round(candleRate * 1000) / 1000;
        double highLowRate = lowPrice != 0 ? (highPrice - lowPrice) / lowPrice : 0;
        highLowRate = (double)Math.round(highLowRate * 1000) / 1000;

        Log.d(TAG, "[DEBUG] tacticalToBuy Candle - marketId: " + key
                + " highPrice: " + mZeroFormat.format(highPrice)
                + " closePrice: " + mZeroFormat.format(closePrice)
                + " openPrice: " + mZeroFormat.format(openPrice)
                + " lowPrice: " + mZeroFormat.format(lowPrice)
                + " candleRate: " + mPercentFormat.format(candleRate)
                + " highLowRate: " + mPercentFormat.format(highLowRate)
                + " upperTailGapPrice: " + mZeroFormat.format(upperTailGapPrice)
                + " lowerTailGapPrice: " + mZeroFormat.format(lowerTailGapPrice)
                + " bodyGapPrice: " + mZeroFormat.format(bodyGapPrice)
                + " upperTailRate: " + mPercentFormat.format(upperTailBodyRate)
                + " lowerTailRate: " + mPercentFormat.format(lowerTailBodyRate)
                + " upperLowerTailRate: " + mPercentFormat.format(upperLowerTailRate)
                + " getTickCounts: " + coinInfo.getTickCounts()
        );

        // type0 : HHCO
        double type0 = (Math.pow(highPrice, 2) + Math.pow(highPrice, 2) + Math.pow(closePrice, 2) + Math.pow(openPrice, 2)) / 4;
        double priceTypeHHCO = CoinInfo.convertPrice(Math.sqrt(type0));

        // type1 : HCO
        double type1 = (Math.pow(highPrice, 2) + Math.pow(closePrice, 2) + Math.pow(openPrice, 2)) / 3;
        double priceTypeHCO = CoinInfo.convertPrice(Math.sqrt(type1));

        // type2 : HCOL
        double type2 = (Math.pow(highPrice, 2) + Math.pow(closePrice, 2) + Math.pow(openPrice, 2) + Math.pow(lowPrice, 2)) / 4;
        double priceTypeHCOL = CoinInfo.convertPrice(Math.sqrt(type2));

        // type3 : COL
        double type3 = (Math.pow(closePrice, 2) + Math.pow(openPrice, 2) + Math.pow(lowPrice, 2)) / 3;
        double priceTypeCOL = CoinInfo.convertPrice(Math.sqrt(type3));

        // type4 : COLL
        double type4 = (Math.pow(closePrice, 2) + Math.pow(openPrice, 2) + Math.pow(lowPrice, 2) + Math.pow(lowPrice, 2)) / 4;
        double priceTypeCOLL = CoinInfo.convertPrice(Math.sqrt(type4));


        boolean isBuy = false;
        if (candleRate >= mMonitorRate || highLowRate >= mMonitorRate) {
            if (bodyGapPrice != 0) {
                if (upperTailBodyRate == 0 && lowerTailBodyRate == 0) {
                    if (coinInfo.getTickCounts() > mMonitorTick * 2) {
                        toBuyPrice = priceTypeHHCO;
                    } else {
                        toBuyPrice = priceTypeHCO;
                    }
                    volume = (mPriceAmount / toBuyPrice);
                    isBuy = true;
                    Log.d(TAG, "[DEBUG] tacticalToBuy 0 HHCO/HCO - !!!! marketId: " + key
                            + " buyPrice: " + mZeroFormat.format(toBuyPrice)
                            + " volume: " + mExtendZeroFormat.format(volume)
                            + " priceAmount: " + mZeroFormat.format(mPriceAmount)
                    );
                } else if (upperTailBodyRate < 0.25 && upperTailBodyRate >= 0 && lowerTailBodyRate == 0) {
                    toBuyPrice = priceTypeHCO;
                    volume = (mPriceAmount / toBuyPrice);
                    isBuy = true;
                    Log.d(TAG, "[DEBUG] tacticalToBuy 1 HCO - !!!! marketId: " + key
                            + " buyPrice: " + mZeroFormat.format(toBuyPrice)
                            + " volume: " + mExtendZeroFormat.format(volume)
                            + " priceAmount: " + mZeroFormat.format(mPriceAmount)
                    );
                } else if (upperTailBodyRate < 0.5 && upperTailBodyRate >= 0.25 && lowerTailBodyRate == 0) {
                    toBuyPrice = priceTypeHCOL;
                    volume = (mPriceAmount / toBuyPrice);
                    isBuy = true;
                    Log.d(TAG, "[DEBUG] tacticalToBuy 2 HCOL - !!!! marketId: " + key
                            + " buyPrice: " + mZeroFormat.format(toBuyPrice)
                            + " volume: " + mExtendZeroFormat.format(volume)
                            + " priceAmount: " + mZeroFormat.format(mPriceAmount)
                    );
                } else if (upperTailBodyRate < 0.33 && upperTailBodyRate >= 0 && lowerTailBodyRate >= 3) {
                    toBuyPrice = priceTypeHCO;
                    volume = (mPriceAmount / toBuyPrice);
                    isBuy = true;
                    Log.d(TAG, "[DEBUG] tacticalToBuy 3 HCO - !!!! marketId: " + key
                            + " buyPrice: " + mZeroFormat.format(toBuyPrice)
                            + " volume: " + mExtendZeroFormat.format(volume)
                            + " priceAmount: " + mZeroFormat.format(mPriceAmount)
                    );
                } else if (upperTailBodyRate < 0.33 && upperTailBodyRate >= 0 && lowerTailBodyRate >= 1) {
                    toBuyPrice = priceTypeHCOL;
                    volume = (mPriceAmount / toBuyPrice);
                    isBuy = true;
                    Log.d(TAG, "[DEBUG] tacticalToBuy 4 HCOL - !!!! marketId: " + key
                            + " buyPrice: " + mZeroFormat.format(toBuyPrice)
                            + " volume: " + mExtendZeroFormat.format(volume)
                            + " priceAmount: " + mZeroFormat.format(mPriceAmount)
                    );
                } else if (upperTailBodyRate < 0.33 && upperTailBodyRate >= 0
                        && lowerTailBodyRate < 0.33 && lowerTailBodyRate >= 0) {
                    toBuyPrice = priceTypeHCOL;
                    volume = (mPriceAmount / toBuyPrice);
                    isBuy = true;
                    Log.d(TAG, "[DEBUG] tacticalToBuy 5 HCOL - !!!! marketId: " + key
                            + " buyPrice: " + mZeroFormat.format(toBuyPrice)
                            + " volume: " + mExtendZeroFormat.format(volume)
                            + " priceAmount: " + mZeroFormat.format(mPriceAmount)
                    );
                }
            } else {
                if (upperLowerTailRate >= -1 && upperLowerTailRate < -0.75) {
                    toBuyPrice = priceTypeCOL;
                    volume = (mPriceAmount / toBuyPrice);
                    isBuy = true;
                    Log.d(TAG, "[DEBUG] tacticalToBuy 6 COL - !!!! marketId: " + key
                            + " buyPrice: " + mZeroFormat.format(toBuyPrice)
                            + " volume: " + mExtendZeroFormat.format(volume)
                            + " priceAmount: " + mZeroFormat.format(mPriceAmount)
                    );
                } else if (upperLowerTailRate >= -0.75 && upperLowerTailRate < -0.5) {
                    toBuyPrice = priceTypeCOLL;
                    volume = (mPriceAmount / toBuyPrice);
                    isBuy = true;
                    Log.d(TAG, "[DEBUG] tacticalToBuy 7 COLL - !!!! marketId: " + key
                            + " buyPrice: " + mZeroFormat.format(toBuyPrice)
                            + " volume: " + mExtendZeroFormat.format(volume)
                            + " priceAmount: " + mZeroFormat.format(mPriceAmount)
                    );
                }
            }
        }

        if (isBuy) {
            mPreventReset = true;
            String uuid = UUID.randomUUID().toString();
            Post post = new Post(key, "bid", Double.toString(volume), Double.toString(toBuyPrice), "limit", uuid);
            registerProcess(UPDATE_POST_ORDER_INFO, post);
            coinInfo.setMarketId(key);
            coinInfo.setBuyPrice(toBuyPrice);
            coinInfo.setUuid(uuid);
            coinInfo.setStatus(CoinInfo.READY);
            mBuyingItemMapInfo.put(key, coinInfo);
        }
    }

    private void updateTradeMapInfoByTicker(String key, Ticker ticker) {

        double lowPrice = 0;
        double highPrice = 0;

        double currentPrice = 0;
        double diffPrice = 0;
        double diffPriceRate = 0;

        double openPrice = 0;
        double closePrice = 0;
        double priceChangedRate = 0;
        double tickCount = 0;

        if (ticker != null) {
            Deque<TradeInfo> tradeInfoQueue = mTradeMapInfo.get(key);
            if (tradeInfoQueue != null) {
                TradeInfo newTradeInfo = new TradeInfo(key,
                        tradeInfoQueue.getLast().getTradeDateUtc(),
                        tradeInfoQueue.getLast().getTradeTimeUtc(),
                        ticker.getTimestamp(),
                        ticker.getTradePrice(),
                        ticker.getTradeVolume(),
                        ticker.getPrevClosingPrice(),
                        ticker.getChangePrice(),
                        tradeInfoQueue.getLast().getAskBid(),
                        tradeInfoQueue.getLast().getSequentialId()
                );
                tradeInfoQueue.offer(newTradeInfo);
                Iterator<TradeInfo> removeIterator = tradeInfoQueue.iterator();
                while (removeIterator.hasNext()) {
                    TradeInfo tradeInfo = removeIterator.next();
                    if (tradeInfoQueue.peekLast().getTimestamp() - tradeInfo.getTimestamp() > mMonitorTime) {
                        removeIterator.remove();
                    } else {
                        double price = tradeInfo.getTradePrice().doubleValue();
                        lowPrice = lowPrice == 0 ? price : Math.min(lowPrice, price);
                        highPrice = Math.max(highPrice, price);
                    }
                }
                mTradeMapInfo.put(key, tradeInfoQueue);

                openPrice = tradeInfoQueue.getFirst().getTradePrice().doubleValue();
                closePrice = tradeInfoQueue.getLast().getTradePrice().doubleValue();
                priceChangedRate = openPrice != 0 ? (closePrice - openPrice) / openPrice : 0;
                tickCount = tradeInfoQueue.size();

                CoinInfo coinInfo = mBuyingItemMapInfo.get(key);
                if (coinInfo != null) {
                    currentPrice = ticker.getTradePrice().doubleValue();
                    diffPrice = currentPrice - coinInfo.getClosePrice();
                    diffPriceRate = diffPrice / coinInfo.getClosePrice();
                    diffPriceRate = (double)Math.round(diffPriceRate * 1000) / 1000;

                    if (coinInfo.getStatus().equals(CoinInfo.BUY)) {
                        long duration = System.currentTimeMillis() - coinInfo.getBuyTime();
                        if (duration >= mMonitorTime) {
                            coinInfo.setMaxProfitRate(highPrice);
                        }
                    }
                    coinInfo.setMarketId(key);
                    coinInfo.setOpenPrice(openPrice);
                    coinInfo.setClosePrice(closePrice);
                    coinInfo.setHighPrice(highPrice);
                    coinInfo.setLowPrice(lowPrice);
                    coinInfo.setTickCounts(tickCount);

                    mBuyingItemMapInfo.put(key, coinInfo);

                    mCoinListAdapter.setMonitoringItems(mMonitorKeyList);
                    mBuyingListAdapter.setBuyingItems(mBuyingItemKeyList);
                    mResultListAdapter.setResultItems(mResultListInfo);

                    Log.d(TAG, "updateTradeMapInfoByTicker - key: " + key
                            + " high: " + mZeroFormat.format(coinInfo.getHighPrice())
                            + " close: " + mZeroFormat.format(coinInfo.getClosePrice())
                            + " open: " + mZeroFormat.format(coinInfo.getOpenPrice())
                            + " low: " + mZeroFormat.format(coinInfo.getLowPrice())
                            + " getBuyPrice: " + mZeroFormat.format(coinInfo.getBuyPrice())
                            + " priceChangedRate: " + mPercentFormat.format(priceChangedRate)
                            + " getMaxProfitRate: " + mPercentFormat.format(coinInfo.getMaxProfitRate())
                            + " getTickCounts: " + coinInfo.getTickCounts()
                    );
                }
            }
        } else {
            CoinInfo coinInfo = mBuyingItemMapInfo.get(key);
            if (coinInfo != null) {
                currentPrice = coinInfo.getClosePrice();
            }
        }

        cancelBuyItemList(key, currentPrice);

        tacticalToSell(key, currentPrice, diffPriceRate);
    }

    private void tacticalToSell(String key, double currentPrice, double diffPriceRate) {
        CoinInfo coinInfo = mBuyingItemMapInfo.get(key);
        if (mBuyingItemKeyList.contains(key) && coinInfo != null && coinInfo.getStatus().equals(CoinInfo.BUY)) {
            // Post to Sell
            double toBuyPrice = coinInfo.getBuyPrice();
            double changedPrice = currentPrice - toBuyPrice;
            double changedRate = changedPrice / toBuyPrice;
            changedRate = (double)Math.round(changedRate * 1000) / 1000;
            double profitRate = changedRate - coinInfo.getMaxProfitRate();
            profitRate = (double)Math.round(profitRate * 1000) / 1000;
            double maxProfitRate = coinInfo.getMaxProfitRate();

            double decisionRate = mMonitorRate / (mMonitorTime / (60 * 1000));

            long duration = System.currentTimeMillis() - coinInfo.getBuyTime();

            Log.d(TAG, "[DEBUG] tacticalToSell Calc : " + key
                    + " currentPrice: " + mZeroFormat.format(currentPrice)
                    + " buyPrice: " + mZeroFormat.format(toBuyPrice)
                    + " changedPrice: " + mZeroFormat.format(changedPrice)
                    + " changedRate: " + mPercentFormat.format(changedRate)
                    + " profitRate: " + mPercentFormat.format(profitRate)
                    + " maxProfitRate: " + mPercentFormat.format(maxProfitRate)
                    + " decisionRate: " + mPercentFormat.format(decisionRate)
                    + " duration: " + mTimeFormat.format(duration)
            );


            if (duration < mMonitorTime && changedRate >= decisionRate * -1.5) {
                Log.d(TAG, "tacticalToSell SELL - skip : " + key);
                return;
            }

            boolean isSell = false;
            if (coinInfo.getMaxProfitRate() > decisionRate * 16) {
                if ((profitRate < decisionRate * -3)) {
                    isSell = true;
                    Log.d(TAG, "[DEBUG] tacticalToSell SELL - Take a profit(Type1) : " + key);
                }
            } else if (coinInfo.getMaxProfitRate() > decisionRate * 8) {
                if ((profitRate < decisionRate * -2.5)) {
                    isSell = true;
                    Log.d(TAG, "[DEBUG] tacticalToSell SELL - Take a profit(Type2) : " + key);
                }
            } else if (coinInfo.getMaxProfitRate() > decisionRate * 4) {
                if ((profitRate < decisionRate * -1.75)) {
                    isSell = true;
                    Log.d(TAG, "[DEBUG] tacticalToSell SELL - Take a profit(Type3) : " + key);
                }
            } else if (coinInfo.getMaxProfitRate() > decisionRate * 2) {
                if ((profitRate < decisionRate * -1)) {
                    isSell = true;
                    Log.d(TAG, "[DEBUG] tacticalToSell SELL - Take a profit(Type4) : " + key);
                }
            } else if (coinInfo.getMaxProfitRate() <= decisionRate * 2 && coinInfo.getMaxProfitRate() > decisionRate * 0.5) {
                if (coinInfo.getMaxProfitRate() >= decisionRate) {
                    if ((profitRate < decisionRate * -0.75)) {
                        isSell = true;
                        Log.d(TAG, "[DEBUG] tacticalToSell SELL - Take a profit(Type5) : " + key);
                    }
                } else {
                    // Stop Loss 1
                    if ((profitRate < decisionRate * -0.5 && diffPriceRate < decisionRate * -0.5)) {
                        isSell = true;
                        Log.d(TAG, "[DEBUG] tacticalToSell SELL - Stop Loss(Type1) : " + key);
                    } else {
                        if (duration < mMonitorTime * 2 && changedRate < decisionRate * -0.5) {
                            isSell = true;
                            Log.d(TAG, "[DEBUG] tacticalToSell SELL - Stop Loss(Type2) : " + key);
                        } else if (changedRate < 0) {
                            isSell = true;
                            Log.d(TAG, "[DEBUG] tacticalToSell SELL - Stop Loss(Type3) : " + key);
                        }
                    }
                }
            }  else {
                // Stop Loss 2
                if (duration < mMonitorTime * 2 && changedRate < decisionRate * -0.5) {
                    isSell = true;
                    Log.d(TAG, "[DEBUG] tacticalToSell SELL - Stop Loss(Type4) : " + key);
                } else if (changedRate < 0) {
                    isSell = true;
                    Log.d(TAG, "[DEBUG] tacticalToSell SELL - Stop Loss(Type5) : " + key);
                }
            }

            if (isSell) {
                ResponseOrder order = mResponseOrderInfoMap.get(key);
                if (order != null && key.equals(order.getMarket())
                        && order.getSide().equals("bid")
                        && order.getState().equals(Post.DONE)) {
                    String uuid = UUID.randomUUID().toString();
                    Post postSell = new Post(key, "ask",
                            coinInfo.isPartialBuy() ? order.getExecutedVolume().toString() : order.getVolume().toString(),
                            null, "market", uuid);
                    registerProcess(UPDATE_POST_ORDER_INFO, postSell);
                    order.setUuid(uuid);
                    mResponseOrderInfoMap.put(key, order);
                    Log.d(TAG, "[DEBUG] tacticalToSell SELL !!!: " + key
                            + " price: " + mZeroFormat.format(currentPrice)
                            + " isPartialBuy: " + coinInfo.isPartialBuy()
                            + " volume: " + mExtendZeroFormat.format(coinInfo.isPartialBuy() ? order.getExecutedVolume().doubleValue() : order.getVolume().doubleValue())
                            + " uuid: " + uuid
                    );
                }
            } else if (duration > mMonitorTime * 3
                    && coinInfo.getMaxProfitRate() <= decisionRate * 0.5
                    && profitRate < decisionRate * -0.5) {
                ResponseOrder order = mResponseOrderInfoMap.get(key);
                if (order != null && key.equals(order.getMarket())
                        && order.getSide().equals("bid")
                        && order.getState().equals(Post.DONE)) {
                    Log.d(TAG, "[DEBUG] tacticalToSell SELL - Stop Loss(expired short time)  : " + key);
                    String uuid = UUID.randomUUID().toString();
                    Post postSell = new Post(key, "ask",
                            coinInfo.isPartialBuy() ? order.getExecutedVolume().toString() : order.getVolume().toString(),
                            null, "market", uuid);
                    registerProcess(UPDATE_POST_ORDER_INFO, postSell);
                    order.setUuid(uuid);
                    mResponseOrderInfoMap.put(key, order);
                }

            } else if (duration > mMonitorTime * 6
                    && coinInfo.getMaxProfitRate() <= decisionRate * 2
                    && (coinInfo.getTickCounts() < mMonitorTick)) {
                ResponseOrder order = mResponseOrderInfoMap.get(key);
                if (order != null && key.equals(order.getMarket())
                        && order.getSide().equals("bid")
                        && order.getState().equals(Post.DONE)) {
                    Log.d(TAG, "[DEBUG] tacticalToSell SELL - Stop Loss(expired long time)  : " + key);
                    String uuid = UUID.randomUUID().toString();
                    Post postSell = new Post(key, "ask",
                            coinInfo.isPartialBuy() ? order.getExecutedVolume().toString() : order.getVolume().toString(),
                            null, "market", uuid);
                    registerProcess(UPDATE_POST_ORDER_INFO, postSell);
                    order.setUuid(uuid);
                    mResponseOrderInfoMap.put(key, order);
                }

            }
        }
    }

    private void cancelBuyItemList(String key, double currentPrice) {
        CoinInfo coinInfo = mBuyingItemMapInfo.get(key);
        if (mBuyingItemKeyList.contains(key)
                && coinInfo != null && (coinInfo.getStatus().equals(CoinInfo.WAITING)
                || (coinInfo.getStatus().equals(CoinInfo.BUY) && coinInfo.isPartialBuy()))) {
            // Request to Cancel.
            double toBuyPrice = coinInfo.getBuyPrice();
            long duration = System.currentTimeMillis() - coinInfo.getWaitTime();
            double decisionRate = mMonitorRate / (mMonitorTime / (60 * 1000));

            if (toBuyPrice > currentPrice || duration > mMonitorTime) {
                double changedPrice = currentPrice - toBuyPrice;
                double changedRate = changedPrice / toBuyPrice;
                changedRate = (double)Math.round(changedRate * 1000) / 1000;

                if (changedRate > decisionRate * 2 || duration > mMonitorTime) {
                    ResponseOrder order = mResponseOrderInfoMap.get(key);
                    if (order != null && order.getMarket().equals(key)
                            && order.getSide().equals("bid")
                            && order.getState().equals(Post.WAIT)) {
                        registerProcess(UPDATE_DELETE_ORDER_INFO, order.getUuid());
                        Log.d(TAG, "[DEBUG] cancelBuyItemList Cancel - !!!! : " + key
                                + " price: " + mZeroFormat.format(currentPrice)
                                + " changedRate: " + mPercentFormat.format(changedRate)
                                + " duration: " + mTimeFormat.format(duration)
                                + " uuid: "+ order.getUuid()
                        );
                    }
                }
            }
        }
    }


    private void updateResponseOrderInfo(ResponseOrder orderInfo) {
        if (orderInfo == null) {
            return;
        }

        String key = orderInfo.getMarket();

        CoinInfo coinInfo = mBuyingItemMapInfo.get(key);
        if (coinInfo == null) {
            return;
        }

        // WAIT
        if (orderInfo.getState().equals(Post.WAIT) && orderInfo.getSide().equals("bid")) {
            if (!mBuyingItemKeyList.contains(key)) {
                if (orderInfo.getVolume() != null && orderInfo.getRemainingVolume() != null &&
                        orderInfo.getVolume().doubleValue() != orderInfo.getRemainingVolume().doubleValue()) {
                    Log.d(TAG, "updateResponseOrderInfo: WAIT setPartialBuy true");
                    coinInfo.setPartialBuy(true);
                } else {
                    Log.d(TAG, "updateResponseOrderInfo: WAIT setPartialBuy false");
                    coinInfo.setPartialBuy(false);
                }
                mPreventReset = false;
                coinInfo.setMarketId(key);
                coinInfo.setStatus(CoinInfo.WAITING);
                coinInfo.setWaitTime(System.currentTimeMillis());
                mBuyingItemMapInfo.put(key, coinInfo);

                mResponseOrderInfoMap.put(key, orderInfo);
                mBuyingItemKeyList.add(key);
                mBuyingListAdapter.setBuyingItems(mBuyingItemKeyList);
                mMonitorKeyList.remove(key);
                mCoinListAdapter.setMonitoringItems(mMonitorKeyList);
                Log.d(TAG, "[DEBUG] updateResponseOrderInfo WAIT - !!!! marketId: " + key
                        + " price: " + mZeroFormat.format(coinInfo.getBuyPrice())
                        + " volume: " + mExtendZeroFormat.format(orderInfo.getVolume() != null ? orderInfo.getVolume().doubleValue() : 0)
                        + " order price: " + mZeroFormat.format(orderInfo.getPrice() != null ? orderInfo.getPrice().doubleValue() : 0)
                        + " avg price: " + mZeroFormat.format(orderInfo.getAvgPrice() != null ? orderInfo.getAvgPrice().doubleValue() : 0)
                        + " uuid: "+ orderInfo.getUuid()
                );
            }
        }

        // BUY
        if (orderInfo.getState().equals(Post.DONE) && orderInfo.getSide().equals("bid")) {
            registerPeriodicUpdate(UPDATE_TICKER_INFO, key);
            removeMonitoringPeriodicUpdate(UPDATE_SEARCH_ORDER_INFO, key);
            if (!mBuyingItemKeyList.contains(key)) {
                mBuyingItemKeyList.add(key);
                mPreventReset = false;
            }

            ResponseOrder prevOrder = mResponseOrderInfoMap.get(key);
            if (prevOrder != null && prevOrder.getMarket().equals(key)
                    && prevOrder.getSide().equals("bid")
                    && prevOrder.getState().equals(Post.DONE)) {
                double volume = prevOrder.getVolume().doubleValue();
                if (orderInfo.getVolume() != null) {
                    volume += orderInfo.getVolume().doubleValue();
                }
                Log.d(TAG, "updateResponseOrderInfo: duplicate BUY key : " + key
                        + " volume - : " + mExtendZeroFormat.format(volume)
                        + " order volume - : " + mExtendZeroFormat.format(orderInfo.getVolume().doubleValue()));
                orderInfo.setVolume(volume);
                coinInfo.setVolume(volume);
            } else {
                coinInfo.setVolume(orderInfo.getVolume() != null ? orderInfo.getVolume().doubleValue() : 0);
            }

            if (orderInfo.getRemainingVolume() != null && orderInfo.getRemainingVolume().doubleValue() != 0) {
                Log.d(TAG, "updateResponseOrderInfo: DONE setPartialBuy true");
                coinInfo.setPartialBuy(true);
            } else {
                Log.d(TAG, "updateResponseOrderInfo: DONE setPartialBuy false");
                coinInfo.setPartialBuy(false);
            }
            coinInfo.setMarketId(key);
            coinInfo.setStatus(CoinInfo.BUY);
            coinInfo.setBuyTime(System.currentTimeMillis());
            coinInfo.setMaxProfitRate(coinInfo.getBuyPrice());

            mBuyingItemMapInfo.put(key, coinInfo);

            mResponseOrderInfoMap.put(key, orderInfo);
            mBuyingListAdapter.setBuyingItems(mBuyingItemKeyList);
            mMonitorKeyList.remove(key);
            mCoinListAdapter.setMonitoringItems(mMonitorKeyList);

            Log.d(TAG, "[DEBUG] updateResponseOrderInfo BUY - !!!! marketId: " + key
                    + " buy price: " + mZeroFormat.format(coinInfo.getBuyPrice())
                    + " volume: " + mExtendZeroFormat.format(orderInfo.getVolume() != null ? orderInfo.getVolume().doubleValue() : 0)
                    + " order price: " + mZeroFormat.format(orderInfo.getPrice() != null ? orderInfo.getPrice().doubleValue() : 0)
                    + " avg price: " + mZeroFormat.format(orderInfo.getAvgPrice() != null ? orderInfo.getAvgPrice().doubleValue() : 0)
                    + " uuid: "+ orderInfo.getUuid()
            );
        }

        // SELL
        if (orderInfo.getState().equals(Post.DONE) && orderInfo.getSide().equals("ask")) {
            mIsShortMoney = false;
            coinInfo.setMarketId(key);
            coinInfo.setStatus(CoinInfo.SELL);
            coinInfo.setSellTime(System.currentTimeMillis());
            Deque<TradeInfo> tradeInfo = mTradeMapInfo.get(key);
            double sellPrice = tradeInfo != null &&  tradeInfo.getLast().getTradePrice() != null ? tradeInfo.getLast().getTradePrice().doubleValue() : 0;
            coinInfo.setSellPrice(CoinInfo.convertSellPrice(sellPrice));

            mResultListInfo.add(coinInfo);
            mResultListAdapter.setResultItems(mResultListInfo);

            removeMonitoringPeriodicUpdate(UPDATE_SEARCH_ORDER_INFO, key);
            removeMonitoringPeriodicUpdate(UPDATE_TICKER_INFO, key);

            mResponseOrderInfoMap.remove(key);
            mBuyingItemKeyList.remove(key);
            mBuyingItemMapInfo.remove(key);
            mBuyingListAdapter.setBuyingItems(mBuyingItemKeyList);

            updateProfitInfo();

            Log.d(TAG, "[DEBUG] updateResponseOrderInfo Sell - !!! marketId: " + key
                    + " state: "+ (orderInfo.getState() != null ? orderInfo.getState() : "N/A")
                    + " sell price: "+ sellPrice
                    + " convert sell price: "+ CoinInfo.convertSellPrice(sellPrice)
                    + " volume: " + mExtendZeroFormat.format(orderInfo.getVolume() != null ? orderInfo.getVolume().doubleValue() : 0)
                    + " avg price: " + mZeroFormat.format(orderInfo.getAvgPrice() != null ? orderInfo.getAvgPrice().doubleValue() : 0)
                    + " uuid: " + orderInfo.getUuid()
            );
        }
    }

    private void deleteOrderInfo(ResponseOrder orderInfo) {
        String key = orderInfo.getMarket();
        ResponseOrder order = mResponseOrderInfoMap.get(key);
        CoinInfo coinInfo = mBuyingItemMapInfo.get(key);
        if (orderInfo.getVolume() != null && orderInfo.getRemainingVolume() != null &&
                orderInfo.getVolume().doubleValue() != orderInfo.getRemainingVolume().doubleValue()) {
            coinInfo.setPartialBuy(true);
        } else {
            coinInfo.setPartialBuy(false);
        }
        mBuyingItemMapInfo.put(key, coinInfo);

        if (order != null) {
            if (coinInfo != null) {
                Log.d(TAG, "[DEBUG] deleteOrderInfo - key: " + order.getMarket()
                        + " getState: " + orderInfo.getState()
                        + " getPrice: " + mZeroFormat.format(orderInfo.getPrice() != null ? orderInfo.getPrice().doubleValue() : 0)
                        + " isPartialBuy: " + coinInfo.isPartialBuy()
                        + " uuid: " + orderInfo.getUuid());

                if (!coinInfo.isPartialBuy()) {
                    Log.d(TAG, "deleteOrderInfo setPartialBuy false");
                    if (orderInfo.getState().equals(Post.DONE) || orderInfo.getState().equals(Post.WAIT)) {
                        mIsShortMoney = false;

                        removeMonitoringPeriodicUpdate(UPDATE_SEARCH_ORDER_INFO, key);
                        removeMonitoringPeriodicUpdate(UPDATE_TICKER_INFO, key);

                        mResponseOrderInfoMap.remove(key);
                        mBuyingItemKeyList.remove(key);
                        mBuyingItemMapInfo.remove(key);
                        mBuyingListAdapter.setBuyingItems(mBuyingItemKeyList);
                    }
                } else {
                    Log.d(TAG, "deleteOrderInfo setPartialBuy true");
                    coinInfo.setStatus(CoinInfo.BUY);
                    order.setVolume(order.getVolume());
                    order.setExecutedVolume(orderInfo.getExecutedVolume());
                    order.setRemainingVolume(order.getRemainingVolume());
                    order.setLocked(order.getLocked());
                    order.setTradesCount(order.getTradesCount());

                    mResponseOrderInfoMap.put(key, order);
                    mBuyingItemMapInfo.put(key, coinInfo);
                    mBuyingListAdapter.setBuyingItems(mBuyingItemKeyList);
                }
            }
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

        Thread resetMonitorItemList = new Thread(() -> {
            while (true) {
                long currentTime = System.currentTimeMillis();
                if (mForceReset || !mPreventReset && mBuyingItemKeyList.isEmpty()
                        && (mLastResetTime == 0 || currentTime - mLastResetTime >= RESET_TIMER - RESET_TIMER_GAP - (10 * 1000))) {
                    Log.d(TAG, "Reset Coin Item List");

                    boolean isExistReady = false;
                    Iterator<CoinInfo> delOrderIterator = mBuyingItemMapInfo.values().iterator();
                    while (delOrderIterator.hasNext()) {
                        CoinInfo coinInfo = delOrderIterator.next();
                        String key = coinInfo.getMarketId();
                        if (coinInfo.getStatus().equals(CoinInfo.READY)) {
                            Log.d(TAG, "[DEBUG] Reset Coin Item Delete READY - key : " + key + " delteUUID: " + coinInfo.getUuid());
                            isExistReady = true;
                        }
                    }

                    if (isExistReady) {
                        try {
                            Thread.sleep(10 * 1000);
                        } catch (InterruptedException e) {
                            Log.w(TAG, "InterruptedException sleep timer");
                        }
                        continue;
                    }

                    Iterator<String> order = mResponseOrderInfoMap.keySet().iterator();
                    while (order.hasNext()) {
                        String key = order.next();
                        ResponseOrder responseOrder = mResponseOrderInfoMap.get(key);
                        if (responseOrder != null && responseOrder.getMarket().equals(key)
                                && responseOrder.getSide().equals("bid")
                                && responseOrder.getState().equals(Post.WAIT)) {
                            registerProcess(UPDATE_DELETE_ORDER_INFO, responseOrder.getUuid());

                            mResponseOrderInfoMap.remove(key);
                            mBuyingItemKeyList.remove(key);
                            mBuyingItemMapInfo.remove(key);
                            mBuyingListAdapter.setBuyingItems(mBuyingItemKeyList);
                            mIsShortMoney = false;

                            Log.d(TAG, "[DEBUG] Reset Coin Item Order List ALL Cancel - !!!! : " + key);
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                Log.w(TAG, "InterruptedException sleep timer");
                            }
                        }
                    }

                    if (!mForceReset) {
                        mDayCandleMapInfo.clear();
                    }
                    mForceReset = false;
                    mLastResetTime = System.currentTimeMillis();

                    mMonitorKeyList.clear();
                    if (mCoinItemList != null) {
                        mCoinItemList.clear();
                    }

                    mActivity.getProcessor().stopBackgroundProcessor();
                    mActivity.getProcessor().registerProcess(UPDATE_MARKETS_INFO, null);
                    mActivity.getProcessor().startBackgroundProcessor();

                    try {
                        Thread.sleep(RESET_TIMER_GAP);
                    } catch (InterruptedException e) {
                        Log.w(TAG, "InterruptedException sleep timer");
                    }

                    if (mCoinItemList.size() < mCoinListNum) {
                        mForceReset = true;
                        continue;
                    }

                    try {
                        Thread.sleep(RESET_TIMER - RESET_TIMER_GAP);
                    } catch (InterruptedException e) {
                        Log.w(TAG, "InterruptedException sleep timer");
                    }
                } else {
                    if (!mActivity.getProcessor().isRunningBackgroundProcessor()) {
                        mActivity.getProcessor().startBackgroundProcessor();
                        mActivity.getProcessor().registerProcess(UPDATE_MARKETS_INFO, null);
                    }
                    Log.d(TAG, "Delay Reset Coin Item List");
                    try {
                        Thread.sleep(RESET_TIMER / 4);
                    } catch (InterruptedException e) {
                        Log.w(TAG, "InterruptedException sleep timer");
                    }
                }
            }
        });
        resetMonitorItemList.start();
    }

    private void registerProcess(int type, Post post) {
        if (post == null) {
            return;
        }
        String marketId = post.getMarketId();
        String side = post.getSide();
        String volume = post.getVolume();
        String price = post.getPrice();
        String ord_type = post.getOrdType();
        String identifier = post.getIdentifier();

        mActivity.getProcessor().registerProcess(type, marketId, side, volume, price, ord_type, identifier);
    }

    private void registerProcess(int type, String uuid) {
        if (uuid == null) {
            return;
        }
        mActivity.getProcessor().registerProcess(type, null, null, null, null, null, uuid);
    }

    private void registerProcess(int type, Set<String> keySet) {
        Iterator<String> regIterator = keySet.iterator();
        while (regIterator.hasNext()) {
            String key = regIterator.next();
            if (!key.equals("KRW-KRW")) {
                mActivity.getProcessor().registerPeriodicUpdate(type, key, 1);
            }
        }

    }

    private void registerPeriodicUpdate(List<String> keyList) {
        Iterator<String> regIterator = keyList.iterator();
        while (regIterator.hasNext()) {
            String key = regIterator.next();
            if (!key.equals("KRW-KRW")) {
                Log.d(TAG, "UPDATE_TRADE_INFO - add key: " + key);
                mActivity.getProcessor().registerPeriodicUpdate(UPDATE_TRADE_INFO, key, TRADE_COUNTS);
            }
        }
    }

    private void registerPeriodicUpdate(int type, String key, String identifier) {
        if (!key.equals("KRW-KRW")) {
            mActivity.getProcessor().registerPeriodicUpdate(type, key, identifier);
        }
    }

    private void registerPeriodicUpdate(int type, String key) {
        if (!key.equals("KRW-KRW")) {
            mActivity.getProcessor().registerPeriodicUpdate(type, key);
        }
    }

    private void removeMonitoringPeriodicUpdate(int type, String key) {
        mActivity.getProcessor().removePeriodicUpdate(type, key);
    }

    private void updateProfitInfo() {
        TextView profitRate = mView.findViewById(R.id.profit_rate);
        TextView profitAmount = mView.findViewById(R.id.profit_total_amount);
        if (profitRate != null && profitAmount != null) {
            Iterator<CoinInfo> infoIterator = mResultListInfo.listIterator();
            double sellSum = 0;
            double buySum = 0;
            while (infoIterator.hasNext()) {
                CoinInfo info = infoIterator.next();
                if (info.getStatus().equals(CoinInfo.SELL)) {
                    sellSum += info.getSellAmount();
                    buySum += info.getBuyAmount();
                }
            }
            double diff = sellSum - buySum;
            double rate = diff / buySum;
            profitRate.setText(mPercentFormat.format(rate));
            profitRate.setTextColor(rate > 0 ? Color.RED : rate < 0 ? Color.BLUE : Color.BLACK);
            profitAmount.setText(mNonZeroFormat.format(diff));
            profitAmount.setTextColor(diff > 0 ? Color.RED : diff < 0 ? Color.BLUE : Color.BLACK);
        }
    }

    private class CoinHolder extends RecyclerView.ViewHolder {
        // Result List
        public TextView mName;
        public TextView mStatus;
        public TextView mChangeRate;
        public TextView mBuyPrice;
        public TextView mBuyAmount;
        public TextView mSellPrice;
        public TextView mProfitAmount;
        public TextView mCurrentPrice;
        public TextView mChangeRate1min;
        public TextView mTickCounts;
        public TextView mAmount1Min;

        public CoinHolder(View itemView, int mode) {
            super(itemView);
            if (mode == mBuyingListAdapter.MODE_MONITOR) {
                mName = itemView.findViewById(R.id.name);
                mStatus = itemView.findViewById(R.id.status);
                mCurrentPrice = itemView.findViewById(R.id.current_price);
                mChangeRate = itemView.findViewById(R.id.change_rate);
                mChangeRate1min = itemView.findViewById(R.id.change_rate_1min);
                mTickCounts = itemView.findViewById(R.id.tick_counts);
                mAmount1Min = itemView.findViewById(R.id.amount_1min);
            } else if (mode == mBuyingListAdapter.MODE_WAITING_OR_BUY){
                mName = itemView.findViewById(R.id.name);
                mStatus = itemView.findViewById(R.id.status);
                mCurrentPrice = itemView.findViewById(R.id.current_price);
                mChangeRate = itemView.findViewById(R.id.change_rate);
                mBuyPrice = itemView.findViewById(R.id.buy_price);
                mBuyAmount = itemView.findViewById(R.id.buy_amount);
            } else if (mode == mBuyingListAdapter.MODE_RESULT){
                mName = itemView.findViewById(R.id.name);
                mStatus = itemView.findViewById(R.id.status);
                mChangeRate = itemView.findViewById(R.id.change_rate);
                mBuyPrice = itemView.findViewById(R.id.buy_price);
                mSellPrice = itemView.findViewById(R.id.sell_price);
                mProfitAmount = itemView.findViewById(R.id.profit_amount);
            }
        }
    }

    private class CoinListAdapter extends RecyclerView.Adapter<CoinHolder> {
        private final int MODE_RESULT = 1;
        private final int MODE_WAITING_OR_BUY = 2;
        private final int MODE_MONITOR = 3;

        private List<String> mCoinListInfo = new ArrayList<>();
        private List<String> mBuyingListInfo = new ArrayList<>();
        private List<CoinInfo> mResultListInfo = new ArrayList<>();
        private int mMode;

        public CoinListAdapter(int mode) {
            mMode = mode;
        }

        public void setMonitoringItems(List<String> coinList) {
            mCoinListInfo.clear();
            mCoinListInfo.addAll(coinList);
            notifyDataSetChanged();
        }

        public void setBuyingItems(List<String> coinList) {
            mBuyingListInfo.clear();
            mBuyingListInfo.addAll(coinList);
            notifyDataSetChanged();
        }

        public void setResultItems(List<CoinInfo> coinList) {
            mResultListInfo.clear();
            mResultListInfo.addAll(coinList);
            notifyDataSetChanged();
        }

        @Override
        public CoinHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (mMode == MODE_MONITOR) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.evaluation_coin_item, parent, false);
            } else if (mMode == MODE_WAITING_OR_BUY){
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.evaluation_buying_coin_item, parent, false);
            } else if (mMode == MODE_RESULT) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.evaluation_result_coin_item, parent, false);
            }
            return new CoinHolder(view, mMode);
        }

        @Override
        public void onBindViewHolder(CoinHolder holder, int position) {
            if (mMode == MODE_MONITOR) {
                String key = mCoinListInfo.get(position);
                MarketInfo marketInfo = mMarketsMapInfo.get(key);
                if (marketInfo != null) {
                    holder.mName.setText(marketInfo.getKorean_name());
                }
                TradeInfo lastTradeInfo = mTradeMapInfo.get(key).getLast();
                TradeInfo firstTradeInfo = mTradeMapInfo.get(key).getFirst();
                int tickCount = mTradeMapInfo.get(key).size();
                double changedPrice1min = lastTradeInfo.getTradePrice().doubleValue() - firstTradeInfo.getTradePrice().doubleValue();
                double changedRate1min = changedPrice1min / firstTradeInfo.getTradePrice().doubleValue();

                double changedPrice = lastTradeInfo.getTradePrice().doubleValue() - lastTradeInfo.getPrevClosingPrice().doubleValue();
                double changedRate = changedPrice / lastTradeInfo.getPrevClosingPrice().doubleValue();
                double amount = 0;
                Iterator<TradeInfo> tradeInfoIterator = mTradeMapInfo.get(key).iterator();
                while (tradeInfoIterator.hasNext()) {
                    TradeInfo tradeInfo = tradeInfoIterator.next();
                    amount += tradeInfo.getTradeVolume().doubleValue();
                }

                if (lastTradeInfo != null && firstTradeInfo != null) {
                    holder.mCurrentPrice.setText(lastTradeInfo.getTradePrice().doubleValue() < 100 ?
                            mZeroFormat.format(lastTradeInfo.getTradePrice().doubleValue()) :
                            mNonZeroFormat.format(lastTradeInfo.getTradePrice().doubleValue()));
                    holder.mChangeRate.setText(mPercentFormat.format(changedRate));
                    holder.mChangeRate.setTextColor(changedRate > 0 ? Color.RED : changedRate < 0 ? Color.BLUE : Color.BLACK);
                    holder.mChangeRate1min.setText(mPercentFormat.format(changedRate1min));
                    holder.mChangeRate1min.setTextColor(changedRate1min > 0 ? Color.RED : changedRate1min < 0 ? Color.BLUE : Color.BLACK);
                    holder.mTickCounts.setText(Integer.toString(tickCount));
                    holder.mAmount1Min.setText(mExtendZeroFormat.format(amount / 1000000));
                }

            } else if (mMode == MODE_WAITING_OR_BUY) {
                String key = mBuyingListInfo.get(position);
                MarketInfo marketInfo = mMarketsMapInfo.get(key);
                if (marketInfo != null) {
                    holder.mName.setText(marketInfo.getKorean_name());
                }
                holder.mName.setText(marketInfo.getKorean_name());

                CoinInfo buyingItem = mBuyingItemMapInfo.get(key);
                if (buyingItem != null
                        && (buyingItem.getStatus().equals(CoinInfo.WAITING)
                        || buyingItem.getStatus().equals(CoinInfo.BUY)
                        || buyingItem.getStatus().equals(CoinInfo.SELL))) {
                    holder.mBuyPrice.setText(buyingItem.getBuyPrice() < 100 ? mZeroFormat.format(buyingItem.getBuyPrice()) : mNonZeroFormat.format(buyingItem.getBuyPrice()));
                    if (!buyingItem.getStatus().equals(CoinInfo.WAITING)) {
                        holder.mBuyAmount.setText(mNonZeroFormat.format(buyingItem.getBuyAmount()));
                    } else {
                        holder.mBuyAmount.setText("N/A");
                    }
                    double currentPrice = buyingItem.getClosePrice();
                    holder.mCurrentPrice.setText(currentPrice < 100 ? mZeroFormat.format(currentPrice) : mNonZeroFormat.format(currentPrice));
                    holder.mStatus.setText(buyingItem.getStatus());

                    double prevPrice = buyingItem.getBuyPrice();
                    double changedPrice = currentPrice - prevPrice;
                    double rate = prevPrice != 0 ? (changedPrice / (double) prevPrice) : 0;
                    if (!buyingItem.getStatus().equals(CoinInfo.WAITING)) {
                        holder.mChangeRate.setText(mPercentFormat.format(rate));
                        holder.mChangeRate.setTextColor(rate > 0 ? Color.RED : rate < 0 ? Color.BLUE : Color.BLACK);

                    } else {
                        holder.mChangeRate.setText("N/A");
                        holder.mChangeRate.setTextColor(Color.GRAY);
                    }
                }
            } else if (mMode == MODE_RESULT) {
                String key = mResultListInfo.get(position).getMarketId();
                MarketInfo marketInfo = mMarketsMapInfo.get(key);
                if (marketInfo != null) {
                    holder.mName.setText(marketInfo.getKorean_name());
                }
                holder.mName.setText(marketInfo.getKorean_name());
                CoinInfo resultItem = mResultListInfo.get(position);
                if (resultItem != null
                        && (resultItem.getStatus().equals(CoinInfo.SELL))) {
                    holder.mBuyPrice.setText(resultItem.getBuyPrice() < 100 ? mZeroFormat.format(resultItem.getBuyPrice()) : mNonZeroFormat.format(resultItem.getBuyPrice()));
                    double changedPrice = resultItem.getSellPrice() - resultItem.getBuyPrice();
                    double prevPrice = resultItem.getBuyPrice();
                    double rate = prevPrice != 0 ? (changedPrice / (double) prevPrice) : 0;
                    holder.mStatus.setText(resultItem.getStatus());
                    holder.mChangeRate.setText(mPercentFormat.format(rate));
                    holder.mChangeRate.setTextColor(rate > 0 ? Color.RED : rate < 0 ? Color.BLUE : Color.BLACK);
                    holder.mSellPrice.setText(resultItem.getSellPrice() < 100 ? mZeroFormat.format(resultItem.getSellPrice()) : mNonZeroFormat.format(resultItem.getSellPrice()));
                    holder.mProfitAmount.setText(mNonZeroFormat.format(resultItem.getProfitAmount()));
                    holder.mProfitAmount.setTextColor(resultItem.getProfitAmount() > 0 ? Color.RED : resultItem.getProfitAmount() < 0 ? Color.BLUE : Color.BLACK);
                }
            }
        }

        @Override
        public int getItemCount() {
            int count = 0;
            if (mMode == MODE_MONITOR) {
                count = mCoinListInfo != null ? mCoinListInfo.size() : 0;
            } else if (mMode == MODE_WAITING_OR_BUY) {
                count = mBuyingListInfo != null ? mBuyingListInfo.size() : 0;
            } else if (mMode == MODE_RESULT) {
                count = mResultListInfo != null ? mResultListInfo.size() : 0;
            }
            return count;
        }
    }

}
