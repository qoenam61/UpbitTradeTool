package com.example.upbitautotrade.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.upbitautotrade.viewmodel.AccountsViewModel;
import com.example.upbitautotrade.viewmodel.CoinEvaluationViewModel;
import com.example.upbitautotrade.viewmodel.UpBitViewModel;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class BackgroundProcessor {
    private static final String TAG = "BackgroundProcessor";

    public static final int UPDATE_ACCOUNTS_INFO = 1;
    public static final int UPDATE_CHANCE_INFO = 2;
    public static final int UPDATE_TICKER_INFO = 4;
    public static final int UPDATE_MARKETS_INFO = 5;
    public static final int UPDATE_MIN_CANDLE_INFO = 7;
    public static final int UPDATE_DAY_CANDLE_INFO = 8;
    public static final int UPDATE_WEEK_CANDLE_INFO = 9;
    public static final int UPDATE_MONTH_CANDLE_INFO = 10;
    public static final int UPDATE_TRADE_INFO = 11;

    private final String PROCESS_UPDATE_KEY = "process_key";

    private final String BUNDLE_KEY_UNIT = "unit";
    private final String BUNDLE_KEY_MARKET_ID = "market_id";
    private final String BUNDLE_KEY_TO = "to";
    private final String BUNDLE_KEY_COUNT = "count";
    private final String BUNDLE_KEY_PRICE_UNIT = "convertingPriceUnit";
    private final String BUNDLE_KEY_CURSOR = "cursor";
    private final String BUNDLE_KEY_DAYS_AGO = "daysAgo";

    private final long PERIODIC_TIME_10 = 10;
    private final long PERIODIC_TIME_50 = 50;
    private final long PERIODIC_TIME_500 = 500;
    private final long PERIODIC_TIME_1000 = 1000;

    private Thread mAccountInfoThread;
    private Thread mChanceInfoThread;
    private Thread mTickerInfoThread;
    private Thread mMarketsInfoThread;
    private Thread mMinCandleInfoThread;
    private Thread mDayCandleInfoThread;
    private Thread mWeekCandleInfoThread;
    private Thread mMonthCandleInfoThread;
    private Thread mTradeInfoThread;

    private UpBitViewModel mViewModel;

    private final Deque<Item> mMarketsInfoTaskList;
    private final List<Item> mAccountInfoTaskList;
    private final List<Item> mChanceInfoTaskList;
    private final List<Item> mTickerInfoTaskList;
    private final List<Item> mMinCandleInfoTaskList;
    private final List<Item> mDayCandleInfoTaskList;
    private final List<Item> mWeekCandleInfoTaskList;
    private final List<Item> mMonthCandleInfoTaskList;
    private final List<Item> mTradeInfoTaskList;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int unit = msg.getData().getInt(BUNDLE_KEY_UNIT + msg.what);
            String marketId = msg.getData().getString(BUNDLE_KEY_MARKET_ID + msg.what);
            String to = msg.getData().getString(BUNDLE_KEY_TO + msg.what);
            int count = msg.getData().getInt(BUNDLE_KEY_COUNT + msg.what);
            String priceUnit = msg.getData().getString(BUNDLE_KEY_PRICE_UNIT + msg.what);
            String cursor = msg.getData().getString(BUNDLE_KEY_CURSOR + msg.what);
            int daysAgo = msg.getData().getInt(BUNDLE_KEY_DAYS_AGO + msg.what);

            switch (msg.what) {
                case UPDATE_MARKETS_INFO:
                    if (mViewModel instanceof AccountsViewModel) {
                        ((AccountsViewModel)mViewModel).searchMarketsInfo(true);
                    } else if (mViewModel instanceof CoinEvaluationViewModel) {
                        ((CoinEvaluationViewModel)mViewModel).searchMarketsInfo(true);
                    }
                    break;
                case UPDATE_ACCOUNTS_INFO:
                    mViewModel.searchAccountsInfo(false);
                    break;
                case UPDATE_CHANCE_INFO:
                    if (mViewModel instanceof AccountsViewModel) {
                        ((AccountsViewModel)mViewModel).searchChanceInfo(marketId);
                    }
                    break;
                case UPDATE_TICKER_INFO:
                    if (mViewModel instanceof AccountsViewModel) {
                        ((AccountsViewModel)mViewModel).searchTickerInfo(marketId);
                    } else if (mViewModel instanceof CoinEvaluationViewModel) {
                        ((CoinEvaluationViewModel)mViewModel).searchTickerInfo(marketId);
                    }
                    break;
                case UPDATE_MIN_CANDLE_INFO:
                    if (mViewModel instanceof CoinEvaluationViewModel) {
                        ((CoinEvaluationViewModel)mViewModel).searchMinCandleInfo(unit, marketId, to, count);
                    }
                    break;
                case UPDATE_DAY_CANDLE_INFO:
                    if (mViewModel instanceof CoinEvaluationViewModel) {
                        ((CoinEvaluationViewModel)mViewModel).searchDayCandleInfo(marketId, to, count, priceUnit);
                    }
                    break;
                case UPDATE_WEEK_CANDLE_INFO:
                    if (mViewModel instanceof CoinEvaluationViewModel) {
                        ((CoinEvaluationViewModel)mViewModel).searchWeekCandleInfo(marketId, to, count);
                    }
                    break;
                case UPDATE_MONTH_CANDLE_INFO:
                    if (mViewModel instanceof CoinEvaluationViewModel) {
                        ((CoinEvaluationViewModel)mViewModel).searchMonthCandleInfo(marketId, to, count);
                    }
                    break;
                case UPDATE_TRADE_INFO:
                    if (mViewModel instanceof CoinEvaluationViewModel) {
                        ((CoinEvaluationViewModel)mViewModel).searchTradeInfo(marketId, to, count, cursor, daysAgo);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public BackgroundProcessor() {
        mMarketsInfoTaskList = new LinkedList<>();
        mAccountInfoTaskList = new ArrayList<>();
        mChanceInfoTaskList = new ArrayList<>();
        mTickerInfoTaskList = new ArrayList<>();
        mMinCandleInfoTaskList = new ArrayList<>();
        mDayCandleInfoTaskList = new ArrayList<>();
        mWeekCandleInfoTaskList = new ArrayList<>();
        mMonthCandleInfoTaskList = new ArrayList<>();
        mTradeInfoTaskList = new ArrayList<>();

    }

    private void sendMessage(Item item) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_MARKET_ID + item.type, item.key);
        bundle.putInt(BUNDLE_KEY_UNIT + item.type, item.unit);
        bundle.putString(BUNDLE_KEY_TO + item.type, item.to);
        bundle.putInt(BUNDLE_KEY_COUNT + item.type, item.count);
        bundle.putString(BUNDLE_KEY_PRICE_UNIT + item.type, item.priceUnit);
        bundle.putString(BUNDLE_KEY_CURSOR + item.type, item.cursor);
        bundle.putInt(BUNDLE_KEY_DAYS_AGO + item.type, item.daysAgo);
        Message message = new Message();
        message.what = item.type;
        message.setData(bundle);
        mHandler.sendMessage(message);
    }

    private void sendPollMessage(Item item, long time) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_MARKET_ID + item.type, item.key);
        bundle.putInt(BUNDLE_KEY_UNIT + item.type, item.unit);
        bundle.putString(BUNDLE_KEY_TO + item.type, item.to);
        bundle.putInt(BUNDLE_KEY_COUNT + item.type, item.count);
        bundle.putString(BUNDLE_KEY_PRICE_UNIT + item.type, item.priceUnit);
        bundle.putString(BUNDLE_KEY_CURSOR + item.type, item.cursor);
        bundle.putInt(BUNDLE_KEY_DAYS_AGO + item.type, item.daysAgo);
        Message message = new Message();
        message.what = item.type;
        message.setData(bundle);
        mHandler.sendMessageAtTime(message, time);
    }

    public void setViewModel(ViewModel viewModel, String accessKey, String secretKey) {
        mViewModel = (UpBitViewModel) viewModel;
        mViewModel.setKey(accessKey, secretKey);
    }

    public void registerProcess(int type, String key) {
        switch(type) {
            case UPDATE_MARKETS_INFO:
                mMarketsInfoTaskList.offer(new Item(type, key));
                break;
            default:
                break;
        }
    }

    public void registerPeriodicUpdate(int type, String key) {
        registerPeriodicUpdate(type, key, 0);
    }

    public void registerPeriodicUpdate(int type, String key, int count) {
        registerPeriodicUpdate(type, key, count, -1);
    }

    public void registerPeriodicUpdate(int type, String key, int count, int unit) {
        registerPeriodicUpdate(type, key, count, unit, -1, null, null, null);
    }

    public void registerPeriodicUpdate(int type, String key, int count, int unit, int daysAgo, String to, String cursor, String priceUnit) {
        Item item = new Item(type, key, count, unit, daysAgo, to, cursor, priceUnit);
        switch(type) {
            case UPDATE_ACCOUNTS_INFO:
                mAccountInfoTaskList.add(item);
                break;
            case UPDATE_CHANCE_INFO:
                mChanceInfoTaskList.add(item);
                break;
            case UPDATE_TICKER_INFO:
                mTickerInfoTaskList.add(item);
                break;
            case UPDATE_MIN_CANDLE_INFO:
                mMinCandleInfoTaskList.add(item);
                break;
            case UPDATE_DAY_CANDLE_INFO:
                mDayCandleInfoTaskList.add(item);
                break;
            case UPDATE_WEEK_CANDLE_INFO:
                mWeekCandleInfoTaskList.add(item);
                break;
            case UPDATE_MONTH_CANDLE_INFO:
                mMonthCandleInfoTaskList.add(item);
                break;
            case UPDATE_TRADE_INFO:
                mTradeInfoTaskList.add(item);
                break;
            default:
                break;
        }
    }

    public void removePeriodicUpdate(int type) {
        removePeriodicUpdate(type, null);
    }

    public void removePeriodicUpdate(int type, String key) {
        switch(type) {
            case UPDATE_ACCOUNTS_INFO:
                if (key == null) {
                    mAccountInfoTaskList.clear();
                } else {
                    Iterator<Item> iterator = mAccountInfoTaskList.iterator();
                    while (iterator.hasNext()) {
                        Item item = iterator.next();
                        if (item.equals(key)) {
                            iterator.remove();
                        }
                    }
                }
                break;
            case UPDATE_CHANCE_INFO:
                if (key == null) {
                    mChanceInfoTaskList.clear();
                } else {
                    Iterator<Item> iterator = mChanceInfoTaskList.iterator();
                    while (iterator.hasNext()) {
                        Item item = iterator.next();
                        if (item.equals(key)) {
                            iterator.remove();
                        }
                    }
                }
                break;
            case UPDATE_TICKER_INFO:
                if (key == null) {
                    mTickerInfoTaskList.clear();
                } else {
                    Iterator<Item> iterator = mTickerInfoTaskList.iterator();
                    while (iterator.hasNext()) {
                        Item item = iterator.next();
                        if (item.equals(key)) {
                            iterator.remove();
                        }
                    }
                }
                break;
            case UPDATE_MIN_CANDLE_INFO:
                if (key == null) {
                    mMinCandleInfoTaskList.clear();
                } else {
                    Iterator<Item> iterator = mMinCandleInfoTaskList.iterator();
                    while (iterator.hasNext()) {
                        Item item = iterator.next();
                        if (item.equals(key)) {
                            iterator.remove();
                        }
                    }
                }
                break;
            case UPDATE_DAY_CANDLE_INFO:
                if (key == null) {
                    mDayCandleInfoTaskList.clear();
                } else {
                    Iterator<Item> iterator = mDayCandleInfoTaskList.iterator();
                    while (iterator.hasNext()) {
                        Item item = iterator.next();
                        if (item.equals(key)) {
                            iterator.remove();
                        }
                    }
                }
                break;
            case UPDATE_WEEK_CANDLE_INFO:
                if (key == null) {
                    mWeekCandleInfoTaskList.clear();
                } else {
                    Iterator<Item> iterator = mWeekCandleInfoTaskList.iterator();
                    while (iterator.hasNext()) {
                        Item item = iterator.next();
                        if (item.equals(key)) {
                            iterator.remove();
                        }
                    }
                }
                break;
            case UPDATE_MONTH_CANDLE_INFO:
                if (key == null) {
                    mMonthCandleInfoTaskList.clear();
                } else {
                    Iterator<Item> iterator = mMonthCandleInfoTaskList.iterator();
                    while (iterator.hasNext()) {
                        Item item = iterator.next();
                        if (item.equals(key)) {
                            iterator.remove();
                        }
                    }
                }
                break;
            case UPDATE_TRADE_INFO:
                if (key == null) {
                    mTradeInfoTaskList.clear();
                } else {
                    Iterator<Item> iterator = mTradeInfoTaskList.iterator();
                    while (iterator.hasNext()) {
                        Item item = iterator.next();
                        if (item.equals(key)) {
                            iterator.remove();
                        }
                    }
                }
                break;
            default:
                break;
        }
        mHandler.removeMessages(type);
    }

    public void startBackgroundProcessor(int type) {
        switch(type) {
            case UPDATE_MARKETS_INFO:
                if (mMarketsInfoThread != null) {
                    mMarketsInfoThread.interrupt();
                }
                mMarketsInfoThread= new Thread(() -> {

                    while (!mMarketsInfoTaskList.isEmpty()) {
                        sendPollMessage(mMarketsInfoTaskList.poll(), PERIODIC_TIME_10);
                    }
                });
                mMarketsInfoThread.start();
                break;
            case UPDATE_ACCOUNTS_INFO:
                if (mAccountInfoThread != null) {
                    mAccountInfoThread.interrupt();
                }
                mAccountInfoThread= new Thread(() -> {
                    Deque<Item> deque = new LinkedList<>();
                    List<Item> list = new ArrayList<>();
                    while (true) {
                        list.clear();
                        list.addAll(mAccountInfoTaskList);
                        Iterator<Item> iterator = list.iterator();
                        while (iterator.hasNext()) {
                            Item item = iterator.next();
                            deque.offer(item);
                        }

                        while (!deque.isEmpty()) {
                            sendMessage(deque.poll());
                            try {
                                Thread.sleep(PERIODIC_TIME_500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                mAccountInfoThread.start();
                break;
            case UPDATE_CHANCE_INFO:
                if (mChanceInfoThread != null) {
                    mChanceInfoThread.interrupt();
                }
                mChanceInfoThread= new Thread(() -> {
                    Deque<Item> deque = new LinkedList<>();
                    List<Item> list = new ArrayList<>();
                    while (true) {
                        list.clear();
                        list.addAll(mChanceInfoTaskList);
                        Iterator<Item> iterator = list.iterator();
                        while (iterator.hasNext()) {
                            Item item = iterator.next();
                            deque.offer(item);
                        }

                        while (!deque.isEmpty()) {
                            sendMessage(deque.poll());
                            try {
                                Thread.sleep(PERIODIC_TIME_50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                mChanceInfoThread.start();
                break;
            case UPDATE_TICKER_INFO:
                if (mTickerInfoThread != null) {
                    mTickerInfoThread.interrupt();
                }
                mTickerInfoThread= new Thread(() -> {
                    Deque<Item> deque = new LinkedList<>();
                    List<Item> list = new ArrayList<>();
                    while (true) {
                        list.clear();
                        list.addAll(mTickerInfoTaskList);
                        Iterator<Item> iterator = list.iterator();
                        while (iterator.hasNext()) {
                            Item item = iterator.next();
                            deque.offer(item);
                        }

                        while (!deque.isEmpty()) {
                            sendMessage(deque.poll());
                            try {
                                Thread.sleep(PERIODIC_TIME_50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                mTickerInfoThread.start();
                break;
            case UPDATE_MIN_CANDLE_INFO:
                if (mMinCandleInfoThread != null) {
                    mMinCandleInfoThread.interrupt();
                }
                mMinCandleInfoThread= new Thread(() -> {
                    Deque<Item> deque = new LinkedList<>();
                    List<Item> list = new ArrayList<>();
                    while (true) {
                        list.clear();
                        list.addAll(mMinCandleInfoTaskList);
                        Iterator<Item> iterator = list.iterator();
                        while (iterator.hasNext()) {
                            Item item = iterator.next();
                            deque.offer(item);
                        }

                        while (!deque.isEmpty()) {
                            sendMessage(deque.poll());
                            try {
                                Thread.sleep(PERIODIC_TIME_1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                mMinCandleInfoThread.start();
                break;
            case UPDATE_DAY_CANDLE_INFO:
                if (mDayCandleInfoThread != null) {
                    mDayCandleInfoThread.interrupt();
                }
                mDayCandleInfoThread= new Thread(() -> {
                    Deque<Item> deque = new LinkedList<>();
                    List<Item> list = new ArrayList<>();
                    while (true) {
                        list.clear();
                        list.addAll(mDayCandleInfoTaskList);
                        Iterator<Item> iterator = list.iterator();
                        while (iterator.hasNext()) {
                            Item item = iterator.next();
                            deque.offer(item);
                        }

                        while (!deque.isEmpty()) {
                            sendMessage(deque.poll());
                            try {
                                Thread.sleep(PERIODIC_TIME_1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                mDayCandleInfoThread.start();
                break;
            case UPDATE_WEEK_CANDLE_INFO:
                if (mWeekCandleInfoThread != null) {
                    mWeekCandleInfoThread.interrupt();
                }
                mWeekCandleInfoThread= new Thread(() -> {
                    Deque<Item> deque = new LinkedList<>();
                    List<Item> list = new ArrayList<>();
                    while (true) {
                        list.clear();
                        list.addAll(mWeekCandleInfoTaskList);
                        Iterator<Item> iterator = list.iterator();
                        while (iterator.hasNext()) {
                            Item item = iterator.next();
                            deque.offer(item);
                        }

                        while (!deque.isEmpty()) {
                            sendMessage(deque.poll());
                            try {
                                Thread.sleep(PERIODIC_TIME_1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                mWeekCandleInfoThread.start();
                break;
            case UPDATE_MONTH_CANDLE_INFO:
                if (mMonthCandleInfoThread != null) {
                    mMonthCandleInfoThread.interrupt();
                }
                mMonthCandleInfoThread= new Thread(() -> {
                    Deque<Item> deque = new LinkedList<>();
                    List<Item> list = new ArrayList<>();
                    while (true) {
                        list.clear();
                        list.addAll(mMonthCandleInfoTaskList);
                        Iterator<Item> iterator = list.iterator();
                        while (iterator.hasNext()) {
                            Item item = iterator.next();
                            deque.offer(item);
                        }

                        while (!deque.isEmpty()) {
                            sendMessage(deque.poll());
                            try {
                                Thread.sleep(PERIODIC_TIME_1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                mMonthCandleInfoThread.start();
                break;
            case UPDATE_TRADE_INFO:
                if (mTradeInfoThread != null) {
                    mTradeInfoThread.interrupt();
                }
                mTradeInfoThread= new Thread(() -> {
                    Deque<Item> deque = new LinkedList<>();
                    List<Item> list = new ArrayList<>();
                    while (true) {
                        list.clear();
                        list.addAll(mTradeInfoTaskList);
                        Iterator<Item> iterator = list.iterator();
                        while (iterator.hasNext()) {
                            Item item = iterator.next();
                            deque.offer(item);
                        }

                        while (!deque.isEmpty()) {
                            sendMessage(deque.poll());
                            try {
                                Thread.sleep(PERIODIC_TIME_50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                mTradeInfoThread.start();
                break;
            default:
                break;
        }
    }

    public void stopBackgroundProcessor() {
        if (mMarketsInfoThread != null) {
            mMarketsInfoThread.interrupt();
        }
        if (mAccountInfoThread != null) {
            mAccountInfoThread.interrupt();
        }
        if (mChanceInfoThread != null) {
            mChanceInfoThread.interrupt();
        }
        if (mTickerInfoThread != null) {
            mTickerInfoThread.interrupt();
        }
        if (mMinCandleInfoThread != null) {
            mMinCandleInfoThread.interrupt();
        }
        if (mDayCandleInfoThread != null) {
            mDayCandleInfoThread.interrupt();
        }
        if (mWeekCandleInfoThread != null) {
            mWeekCandleInfoThread.interrupt();
        }
        if (mMonthCandleInfoThread != null) {
            mMonthCandleInfoThread.interrupt();
        }
        if (mTradeInfoThread != null) {
            mTradeInfoThread.interrupt();
        }
    }

    public AccountsViewModel getAccountsViewModel() {
        return (AccountsViewModel) mViewModel;
    }

    public CoinEvaluationViewModel getCoinEvaluationViewModel() {
        return (CoinEvaluationViewModel) mViewModel;
    }
}
