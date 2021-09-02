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
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    public static final int UPDATE_POST_ORDER_INFO = 12;
    public static final int UPDATE_SEARCH_ORDER_INFO = 13;
    public static final int UPDATE_DELETE_ORDER_INFO = 14;

    private final String BUNDLE_KEY_UNIT = "unit";
    private final String BUNDLE_KEY_MARKET_ID = "market_id";
    private final String BUNDLE_KEY_TO = "to";
    private final String BUNDLE_KEY_COUNT = "count";
    private final String BUNDLE_KEY_PRICE_UNIT = "convertingPriceUnit";
    private final String BUNDLE_KEY_CURSOR = "cursor";
    private final String BUNDLE_KEY_DAYS_AGO = "daysAgo";

    private final String BUNDLE_KEY_SIDE = "side";
    private final String BUNDLE_KEY_VOLUME = "volume";
    private final String BUNDLE_KEY_PRICE = "price";
    private final String BUNDLE_KEY_ORDER_TYPE = "ord_type";
    private final String BUNDLE_KEY_IDENTIFIER = "identifier";

    private UpBitViewModel mViewModel;

    private Map<Integer, TaskList> mProcessTaskMap;

    private Thread mProcessThread;
    private ThreadPoolExecutor mThreadPool;

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

            String side = msg.getData().getString(BUNDLE_KEY_SIDE + msg.what);;
            String volume = msg.getData().getString(BUNDLE_KEY_VOLUME + msg.what);;
            String price = msg.getData().getString(BUNDLE_KEY_PRICE + msg.what);;
            String orderType = msg.getData().getString(BUNDLE_KEY_ORDER_TYPE + msg.what);;
            String identifier = msg.getData().getString(BUNDLE_KEY_IDENTIFIER + msg.what);;

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
                case UPDATE_POST_ORDER_INFO:
                    if (mViewModel instanceof CoinEvaluationViewModel) {
                        ((CoinEvaluationViewModel)mViewModel).postOrderInfo(marketId, side, volume, price, orderType, identifier);
                    }
                    break;
                case UPDATE_SEARCH_ORDER_INFO:
                    if (mViewModel instanceof CoinEvaluationViewModel) {
                        ((CoinEvaluationViewModel)mViewModel).searchOrderInfo(identifier);
                    }
                    break;
                case UPDATE_DELETE_ORDER_INFO:
                    if (mViewModel instanceof CoinEvaluationViewModel) {
                        ((CoinEvaluationViewModel)mViewModel).deleteOrderInfo(identifier);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private boolean mIsRunningBackgroundProcessor = false;

    public BackgroundProcessor() {
        mProcessTaskMap = new HashMap<>();
        makeThreadPoolExecutor();
    }

    private void makeThreadPoolExecutor() {
        mThreadPool = new ThreadPoolExecutor(1, 4, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
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

        bundle.putString(BUNDLE_KEY_SIDE + item.type, item.side);
        bundle.putString(BUNDLE_KEY_VOLUME + item.type,item.volume);
        bundle.putString(BUNDLE_KEY_PRICE + item.type, item.price);
        bundle.putString(BUNDLE_KEY_ORDER_TYPE + item.type, item.ord_type);
        bundle.putString(BUNDLE_KEY_IDENTIFIER + item.type, item.identifier);

        Message message = new Message();
        message.what = item.type;
        message.setData(bundle);
        mHandler.sendMessage(message);
    }

    public void setViewModel(ViewModel viewModel, String accessKey, String secretKey) {
        mViewModel = (UpBitViewModel) viewModel;
        mViewModel.setKey(accessKey, secretKey);
    }

    private class TaskList extends ArrayList<Item> implements Runnable {
        boolean isStop = false;
        public int getType() {
            return size() != 0 ? get(0).type : -1;
        }

        public String getMarketId() {
            return size() != 0 ? get(0).key : "No Id";
        }

        public long getSleepTime() {
            return size() != 0 ? get(0).getSleepTime() : 50;
        }

        public void stop() {
            isStop = true;
        }

        @Override
        public void run() {
            isStop = false;
            Deque<Item> itemDeque = new LinkedList<>();
            itemDeque.addAll(TaskList.this);
            while (!itemDeque.isEmpty() && !isStop) {
                Item item = itemDeque.poll();
                if (item == null) {
                    continue;
                }
//                Log.d(TAG, "[DEBUG] TaskList -type: "+item.type+" marketId: "+item.key+" sleepTime: "+item.getSleepTime());
                switch (item.type) {
                    case UPDATE_MARKETS_INFO:
                    case UPDATE_POST_ORDER_INFO:
                    case UPDATE_DELETE_ORDER_INFO:
                    case UPDATE_MIN_CANDLE_INFO:
                    case UPDATE_DAY_CANDLE_INFO:
                    case UPDATE_WEEK_CANDLE_INFO:
                    case UPDATE_MONTH_CANDLE_INFO:
                        sendMessage(item);
                        clear();
                        break;
                    case UPDATE_ACCOUNTS_INFO:
                    case UPDATE_CHANCE_INFO:
                    case UPDATE_TICKER_INFO:
                    case UPDATE_TRADE_INFO:
                    case UPDATE_SEARCH_ORDER_INFO:
                        sendMessage(item);
                        break;
                    default:
                        break;
                }
                try {
                    Thread.sleep(item.getSleepTime());
                } catch (InterruptedException e) {
                    Log.w(TAG, "Exception to Thread sleep");
                }
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

    public void registerProcess(int type, String key, String identifier) {
        Item item = new Item(type, key, identifier);
        Map<Integer, TaskList> map = mProcessTaskMap;
        if (map.get(type) == null) {
            TaskList taskList = new TaskList();
            taskList.add(item);
            mProcessTaskMap.put(type, taskList);
        } else {
            mProcessTaskMap.get(type).add(item);
        }
    }

    public void registerProcess(int type, String marketId, String side, String volume, String price, String ord_type, String identifier) {
        Item item = new Item(type, marketId, side, volume, price, ord_type, identifier);
        TaskList taskList = new TaskList();
        taskList.add(item);
        Thread thread = new Thread(taskList);
        thread.start();
    }

    public void registerPeriodicUpdate(int type, String key, String identifier) {
        Item item = new Item(type, key, identifier);
        Map<Integer, TaskList> map = mProcessTaskMap;
        if (!map.containsKey(type)) {
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
                mThreadPool.remove(taskList);
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

    public boolean isRunningBackgroundProcessor() {
        return mIsRunningBackgroundProcessor;
    }

    public void startBackgroundProcessor() {
        if (mProcessThread == null) {
            mProcessThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        List<Integer> list = new ArrayList<>();
                        try {
                            list.addAll(mProcessTaskMap.keySet());
                        } catch (ConcurrentModificationException e) {
                            Log.e(TAG, "error: ConcurrentModificationException");
                            continue;
                        }

                        Iterator<Integer> iterator = list.iterator();
                        while (iterator.hasNext()) {
                            int type = iterator.next();
                            TaskList taskList = mProcessTaskMap.get(type);
                            if (taskList != null) {
                                if (!mThreadPool.getQueue().contains(taskList) && mThreadPool.getQueue().size() < (mThreadPool.getMaximumPoolSize() * 2)) {
//                                    Log.d(TAG, "[DEBUG] startBackgroundProcessor -type: "+taskList.getType()+" marketId: "+taskList.getMarketId());
                                    mThreadPool.execute(taskList);
                                    try {
                                        Thread.sleep(taskList.getSleepTime());
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    try {
                                        Thread.sleep(taskList.getSleepTime());
                                    } catch (InterruptedException e) {
                                        Log.w(TAG, "InterruptedException sleep timer");
                                    }
                                }
                            }
                        }
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            Log.w(TAG, "exception Thread sleep");
                        }
                    }
                }
            });
            mProcessThread.start();
            mIsRunningBackgroundProcessor = true;
        }
    }

    public void stopBackgroundProcessor() {
        if (mProcessThread != null) {
            mProcessThread.interrupt();
            mProcessThread = null;
            mIsRunningBackgroundProcessor = false;
        }
        clearProcess();
    }

    public void clearProcess() {
        Map<Integer, TaskList> processTaskMap = mProcessTaskMap;
        Iterator<TaskList> iterator = processTaskMap.values().iterator();
        while (iterator.hasNext()) {
            TaskList taskList = iterator.next();
            if (taskList != null && mThreadPool != null) {
                taskList.stop();
                iterator.remove();
            }
        }
        mThreadPool.shutdownNow();
        mThreadPool.purge();
        mThreadPool.getQueue().clear();
        makeThreadPoolExecutor();
        mProcessTaskMap.clear();
        mHandler.removeCallbacksAndMessages(null);
    }
}
