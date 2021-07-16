package com.example.upbitautotrade.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.upbitautotrade.viewmodel.AccountsViewModel;
import com.example.upbitautotrade.viewmodel.CoinEvaluationViewModel;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class BackgroundProcessor {
    private static final String TAG = "BackgroundProcessor";

    public static final int PERIODIC_UPDATE_ACCOUNTS_INFO = 1;
    public static final int PERIODIC_UPDATE_CHANCE_INFO = 2;
    public static final int PERIODIC_UPDATE_TICKER_INFO_FOR_ACCOUNTS = 3;
    public static final int PERIODIC_UPDATE_TICKER_INFO_FOR_EVALUATION = 4;
    public static final int UPDATE_MARKETS_INFO_FOR_ACCOUNTS = 5;
    public static final int UPDATE_MARKETS_INFO_FOR_COIN_EVALUATION = 6;
    public static final int UPDATE_MIN_CANDLE_INFO_FOR_COIN_EVALUATION = 7;
    public static final int UPDATE_DAY_CANDLE_INFO_FOR_COIN_EVALUATION = 8;
    public static final int UPDATE_WEEK_CANDLE_INFO_FOR_COIN_EVALUATION = 9;
    public static final int UPDATE_MONTH_CANDLE_INFO_FOR_COIN_EVALUATION = 10;
    public static final int UPDATE_TRADE_INFO_FOR_COIN_EVALUATION = 11;

    private final String PROCESS_UPDATE_KEY = "process_key";

    private final String BUNDLE_KEY_UNIT = "unit";
    private final String BUNDLE_KEY_MARKET_ID = "market_id";
    private final String BUNDLE_KEY_TO = "to";
    private final String BUNDLE_KEY_COUNT = "count";
    private final String BUNDLE_KEY_PRICE_UNIT = "convertingPriceUnit";
    private final String BUNDLE_KEY_CURSOR = "cursor";
    private final String BUNDLE_KEY_DAYS_AGO = "daysAgo";

    private final Thread mProcessor;

    private AccountsViewModel mAccountsViewModel;
    private CoinEvaluationViewModel mCoinEvaluationViewModel;


    private long mPeriodicTimer = 30;
    private final Queue<Item> mProcesses;
    private final ArrayList<Item> mUpdateProcesses;

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
                case PERIODIC_UPDATE_ACCOUNTS_INFO:
                    mAccountsViewModel.searchAccountsInfo(false);
                    break;
                case PERIODIC_UPDATE_CHANCE_INFO:
                    mAccountsViewModel.searchChanceInfo(marketId);
                    break;
                case PERIODIC_UPDATE_TICKER_INFO_FOR_ACCOUNTS:
                    mAccountsViewModel.searchTickerInfo(marketId);
                    break;
                case PERIODIC_UPDATE_TICKER_INFO_FOR_EVALUATION:
                    mCoinEvaluationViewModel.searchTickerInfo(marketId);
                    break;
                case UPDATE_MARKETS_INFO_FOR_ACCOUNTS:
                    mAccountsViewModel.searchMarketsInfo(true);
                    break;
                case UPDATE_MARKETS_INFO_FOR_COIN_EVALUATION:
                    mCoinEvaluationViewModel.searchMarketsInfo(true);
                    break;
                case UPDATE_MIN_CANDLE_INFO_FOR_COIN_EVALUATION:
                    mCoinEvaluationViewModel.searchMinCandleInfo(unit, marketId, to, count);
                    break;
                case UPDATE_DAY_CANDLE_INFO_FOR_COIN_EVALUATION:
                    mCoinEvaluationViewModel.searchDayCandleInfo(marketId, to, count, priceUnit);
                    break;
                case UPDATE_WEEK_CANDLE_INFO_FOR_COIN_EVALUATION:
                    mCoinEvaluationViewModel.searchWeekCandleInfo(marketId, to, count);
                    break;
                case UPDATE_MONTH_CANDLE_INFO_FOR_COIN_EVALUATION:
                    mCoinEvaluationViewModel.searchMonthCandleInfo(marketId, to, count);
                    break;
                case UPDATE_TRADE_INFO_FOR_COIN_EVALUATION:
                    mCoinEvaluationViewModel.searchTradeInfo(marketId, to, count, cursor, daysAgo);
                    break;
                default:
                    break;
            }
        }
    };
    private boolean mPauseProcessor = false;

    public BackgroundProcessor(ViewModelStoreOwner owner) {
        mAccountsViewModel = new ViewModelProvider(owner).get(AccountsViewModel.class);
        mCoinEvaluationViewModel = new ViewModelProvider(owner).get(CoinEvaluationViewModel.class);

        mProcesses = new LinkedList<>();
        mUpdateProcesses = new ArrayList<Item>();
        mProcessor = new Thread(() -> {
            while (!mPauseProcessor) {
                try {
                    process();
                    update();
`                    Thread.sleep(mPeriodicTimer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void process() {
        if (mProcesses == null || mProcesses.isEmpty()) {
            return;
        }
        try {
            Item item = mProcesses.poll();
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
            Thread.sleep(mPeriodicTimer);
        } catch (InterruptedException e) {
            Log.w(TAG, "process: Error InterruptedException");
        }
    }

    public void registerProcess(String key, int type) {
        if (mProcesses == null) {
            return;
        }
        mProcesses.offer(new Item(key, type));
    }

    public void registerProcess(int unit, String key, int type,  String to, int count) {
        if (mProcesses == null) {
            return;
        }
        mProcesses.offer(new Item(unit, key, type, to, count));
    }

    public void registerProcess(String key, int type, String to, int count) {
        if (mProcesses == null) {
            return;
        }
        mProcesses.offer(new Item(key, type, to, count));
    }

    public void registerProcess(String key, int type, String to, int count, String priceUnit) {
        if (mProcesses == null) {
            return;
        }
        mProcesses.offer(new Item(key, type, to, count, priceUnit));
    }

    private void update() {
        ArrayList<Item> processes = (ArrayList<Item>)mUpdateProcesses.clone();
        if (processes == null || processes.isEmpty()) {
            return;

        }
        try {
            Iterator<Item> iterator = processes.iterator();
            while (iterator.hasNext()) {
                Item item = iterator.next();
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
                Thread.sleep(mPeriodicTimer);
            }
        } catch (ConcurrentModificationException e) {
            Log.w(TAG, "update: Error ConcurrentModificationException");
        } catch(InterruptedException e) {
            Log.w(TAG, "update: Error InterruptedException");
        } finally {
            processes.clear();
        }

    }

    public void registerPeriodicUpdate(String key, int type) {
        if (mUpdateProcesses == null) {
            return;

        }
        mUpdateProcesses.add(new Item(key, type));
    }

    public void registerPeriodicUpdate(String key, int type, String to, int count, String cursor, int daysAgo) {
        if (mUpdateProcesses == null) {
            return;

        }
        mUpdateProcesses.add(new Item(key, type, to, count, cursor, daysAgo));
    }

    public void registerPeriodicUpdate(int unit, String key, int type, String to, int count) {
        if (mUpdateProcesses == null) {
            return;

        }
        mUpdateProcesses.add(new Item(unit, key, type, to, count));
    }

    public void registerPeriodicUpdate(String key, int type, String to, int count) {
        if (mUpdateProcesses == null) {
            return;

        }
        mUpdateProcesses.add(new Item(key, type, to, count));
    }

    public void registerPeriodicUpdate(String key, int type, String to, int count, String priceUnit) {
        if (mUpdateProcesses == null) {
            return;

        }
        mUpdateProcesses.add(new Item(key, type, to, count, priceUnit));
    }

    public void removePeriodicUpdate(int type) {
        if (mUpdateProcesses == null) {
            return;

        }
        mHandler.removeMessages(type);
        Iterator<Item> iterator = mUpdateProcesses.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            if (item.type == type) {
                iterator.remove();
            }
        }
    }

    public void removePeriodicUpdate(String key) {
        if (mUpdateProcesses == null) {
            return;

        }
        for (Iterator<Item> iterator = mUpdateProcesses.iterator(); iterator.hasNext(); ) {
            Item item = iterator.next();
            if (item.key == key) {
                iterator.remove();
            }
        }
    }

    public void removePeriodicUpdate(String key, int type) {
        if (mUpdateProcesses == null) {
            return;

        }
        for (Iterator<Item> iterator = mUpdateProcesses.iterator(); iterator.hasNext(); ) {
            Item item = iterator.next();
            if (item.key == key) {
                iterator.remove();
            }
        }
    }

    public void startBackgroundProcessor() {
        mPauseProcessor = false;
        mProcessor.start();
    }

    public void stopBackgroundProcessor() {
        mPauseProcessor = true;
        mProcessor.interrupt();
    }

    public void pauseBackgroundProcessor() {
        mPauseProcessor = true;
    }

    public void setPeriodicTimer(long timer) {
        mPeriodicTimer = timer;
    }

    public AccountsViewModel getAccountsViewModel() {
        return mAccountsViewModel;
    }

    public CoinEvaluationViewModel getCoinEvaluationViewModel() {
        return mCoinEvaluationViewModel;
    }
}
