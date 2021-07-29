package com.example.upbitautotrade.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.upbitautotrade.viewmodel.AccountsViewModel;
import com.example.upbitautotrade.viewmodel.CoinEvaluationViewModel;
import com.example.upbitautotrade.viewmodel.UpBitViewModel;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

    private UpBitViewModel mViewModel;

    private final Map<Integer, TaskList> mProcessTaskMap;

    private Handler mProcessHandler = new Handler();
    private Thread mProcessThread;
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
        mProcessTaskMap = new HashMap<>();
    }

    private void sendMessage(Item item, long time) {
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
        mHandler.sendMessageDelayed(message, time);
    }

    public void setViewModel(ViewModel viewModel, String accessKey, String secretKey) {
        mViewModel = (UpBitViewModel) viewModel;
        mViewModel.setKey(accessKey, secretKey);
    }

    private class TaskList extends ArrayList<Item> implements Runnable {

        private Thread taskThread;

        public int getType() {
            return size() != 0 ? get(0).type : -1;
        }

        public String getMarketId() {
            return size() != 0 ? get(0).key : "No Id";
        }

        public long getSleepTime() {
            return size() != 0 ? get(0).getSleepTime() : 17;
        }

        public void stop() {
            if (taskThread != null) {
                taskThread.interrupt();
                taskThread = null;
            }
        }

        public boolean isRunning() {
            return taskThread != null && taskThread.isAlive();
        }

        @Override
        synchronized public void run() {
            if (taskThread == null || !isRunning()) {
                Log.d(TAG, "[DEBUG] run: TaskList new - type: "+getType()+" key: "+getMarketId());
                taskThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Deque<Item> itemDeque = new LinkedList<>();
                        itemDeque.addAll(TaskList.this);
                        while (!itemDeque.isEmpty()) {
                            Item item = itemDeque.poll();
                            Log.d(TAG, "[DEBUG] TaskList - type: " + item.type + " key: " + item.key);
                            switch (item.type) {
                                case UPDATE_MARKETS_INFO:
                                    sendMessage(item, getSleepTime());
                                    Log.d(TAG, "[DEBUG] TaskList - remove: " + item.type + " key: " + item.key);
                                    clear();
                                    break;
                                case UPDATE_ACCOUNTS_INFO:
                                case UPDATE_CHANCE_INFO:
                                case UPDATE_TICKER_INFO:
                                case UPDATE_MIN_CANDLE_INFO:
                                case UPDATE_DAY_CANDLE_INFO:
                                case UPDATE_WEEK_CANDLE_INFO:
                                case UPDATE_MONTH_CANDLE_INFO:
                                case UPDATE_TRADE_INFO:
                                    sendMessage(item, getSleepTime());
                                    break;
                                default:
                                    break;
                            }
                            try {
                                Thread.sleep(getSleepTime());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                taskThread.start();
            } else {
                Log.d(TAG, "[DEBUG] run: not Null or isAlive");
            }
        }
    }

    public void registerProcess(int type, String key) {
        Item item = new Item(type, key);
        Map<Integer, TaskList> map = mProcessTaskMap;
        if (map.get(type) == null) {
            TaskList taskList = new TaskList();
            taskList.add(item);
            mProcessTaskMap.put(type, taskList);
        } else {
            mProcessTaskMap.get(type).add(item);
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

        Map<Integer, TaskList> map = mProcessTaskMap;
        if (!map.containsKey(type)) {
            TaskList taskList = new TaskList();
            taskList.add(item);
            mProcessTaskMap.put(type, taskList);
        } else {
            mProcessTaskMap.get(type).add(item);
        }
    }

    public void removePeriodicUpdate(int type) {
        removePeriodicUpdate(type, null);
    }

    public void removePeriodicUpdate(int type, String key) {
        TaskList taskList = mProcessTaskMap.get(type);
        if (mProcessTaskMap.containsKey(type) && taskList != null) {
            if (key == null) {
                mProcessHandler.removeCallbacks(taskList);
                taskList.stop();
                mProcessTaskMap.remove(type);
                mHandler.removeMessages(type);
            } else {
                Iterator<Item> iterator = taskList.iterator();
                while (iterator.hasNext()) {
                    Item item = iterator.next();
                    if (item.key.equals(key)) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    public void startBackgroundProcessor() {
        if (mProcessThread == null) {
            Log.d(TAG, "[DEBUG] startBackgroundProcessor new");
            mProcessThread = new Thread(new Runnable() {
                @Override
                synchronized public void run() {
                    while (true) {
                        List<Integer> list = new ArrayList<>();
                        list.addAll(mProcessTaskMap.keySet());
                        Iterator<Integer> iterator = list.iterator();
                        while (iterator.hasNext()) {
                            int type = iterator.next();
                            TaskList taskList = mProcessTaskMap.get(type);
                            if (taskList != null) {
                                if (!taskList.isRunning()) {
                                    Log.d(TAG, "[DEBUG] startBackgroundProcessor post - getType: " + taskList.getType() + " getSleepTime: " + taskList.getSleepTime());
                                    mProcessHandler.post(taskList);
                                } else {
                                    Log.d(TAG, "[DEBUG] startBackgroundProcessor remove - getType: " + taskList.getType() + " getSleepTime: " + taskList.getSleepTime());
                                    taskList.stop();
                                    mProcessHandler.removeCallbacks(taskList);
                                }
                            }
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            mProcessThread.start();
        }
    }

    public void stopBackgroundProcessor() {
        if (mProcessThread != null) {
            Log.d(TAG, "[DEBUG] stopBackgroundProcessor");
            mProcessThread.interrupt();
            mProcessThread = null;
        }
        clearProcess();
    }

    public void clearProcess() {
        Iterator<TaskList> iterator = mProcessTaskMap.values().iterator();
        while (iterator.hasNext()) {
            TaskList taskList = iterator.next();
            if (taskList != null) {
                taskList.stop();
                mProcessHandler.removeCallbacks(taskList);
                iterator.remove();
            }
        }
        mProcessTaskMap.clear();

        mProcessHandler.removeCallbacksAndMessages(null);
        mHandler.removeCallbacksAndMessages(null);
    }

    public AccountsViewModel getAccountsViewModel() {
        return (AccountsViewModel) mViewModel;
    }

    public CoinEvaluationViewModel getCoinEvaluationViewModel() {
        return (CoinEvaluationViewModel) mViewModel;
    }
}
