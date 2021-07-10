package com.example.upbitautotrade.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.upbitautotrade.viewmodel.AccountsViewModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class BackgroundProcessor {
    private static final String TAG = "BackgroundProcessor";

    public static final int PERIODIC_UPDATE_ACCOUNTS_INFO = 1;
    public static final int PERIODIC_UPDATE_CHANCE_INFO = 2;
    public static final int PERIODIC_UPDATE_TICKER_INFO = 3;

    private final String PERIODIC_UPDATE_KEY = "periodic_key";

    private final Thread mProcessor;

    private AccountsViewModel mAccountsViewModel;


    private long mPeriodicTimer = 1000;
    private final Queue<Integer> mProcesses;
    private final ArrayList<Item> mUpdateProcesses;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PERIODIC_UPDATE_ACCOUNTS_INFO:
                    mAccountsViewModel.searchAccountsInfo(false);
                    break;
                case PERIODIC_UPDATE_CHANCE_INFO:
                    mAccountsViewModel.searchChanceInfo(msg.getData().getString(PERIODIC_UPDATE_KEY));
                    break;
                case PERIODIC_UPDATE_TICKER_INFO:
                    mAccountsViewModel.searchTickerInfo(msg.getData().getString(PERIODIC_UPDATE_KEY));
                    break;
                default:
                    break;
            }
        }
    };

    public BackgroundProcessor(ViewModelStoreOwner owner) {
        mAccountsViewModel = new ViewModelProvider(owner).get(AccountsViewModel.class);

        mProcesses = new LinkedList<>();
        mUpdateProcesses = new ArrayList<Item>();
        mProcessor = new Thread(() -> {
            try {
                while (true) {
                    process();
                    update();
                    Thread.sleep(mPeriodicTimer);
                }
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void process() {
        if (mProcesses == null || mProcesses.size() <= 0) {
            return;
        }
        mHandler.sendEmptyMessage(mProcesses.poll());
    }

    public void setRegisterProcess(int process) {
        if (mProcesses == null) {
            return;
        }
        mProcesses.offer(process);
    }

    private void update() {
        ArrayList<Item> items = mUpdateProcesses;
        if (items == null) {
            return;

        }
        for (Item item:items) {
            Bundle bundle = new Bundle();
            bundle.putString(PERIODIC_UPDATE_KEY, item.mKey);
            Message message = new Message();
            message.what = item.mType;
            message.setData(bundle);
            mHandler.sendMessage(message);
        }
    }

    public void registerPeriodicUpdate(String key, int type) {
        ArrayList<Item> processes = mUpdateProcesses;
        if (processes == null) {
            return;

        }
        Item item = new Item(key, type);
        processes.add(item);
    }

    public void removePeriodicUpdate(int type) {
        if (mUpdateProcesses == null) {
            return;

        }
        for (Iterator<Item> iterator = mUpdateProcesses.iterator(); iterator.hasNext(); ) {
            Item item = iterator.next();
            if (item.mType == type) {
                iterator.remove();
            }
        }
        mHandler.removeMessages(type);
    }

    public void removePeriodicUpdate(String key) {
        if (mUpdateProcesses == null) {
            return;

        }
        for (Iterator<Item> iterator = mUpdateProcesses.iterator(); iterator.hasNext(); ) {
            Item item = iterator.next();
            if (item.mKey == key) {
                iterator.remove();
            }
        }
    }

    public void startBackgroundProcessor() {
        mProcessor.start();
    }

    public void stopBackgroundProcessor() {
        mProcessor.interrupt();
    }

    public void setPeriodicTimer(long timer) {
        mPeriodicTimer = timer;
    }

    public AccountsViewModel getAccountsViewModel() {
        return mAccountsViewModel;
    }

    private class Item {
        String mKey;
        int mType;

        public Item(String key, int type) {
            mKey = key;
            mType = type;
        }
    }
}
