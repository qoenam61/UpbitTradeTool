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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class BackgroundProcessor {
    private static final String TAG = "BackgroundProcessor";

    public static final int PERIODIC_UPDATE_ACCOUNTS_INFO = 1;
    public static final int PERIODIC_UPDATE_CHANCE_INFO = 2;

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
                    Log.d(TAG, "[DEBUG] PERIODIC_UPDATE_ACCOUNTS_INFO");
                    mAccountsViewModel.searchAccountsInfo(false);
                    break;
                case PERIODIC_UPDATE_CHANCE_INFO:
                    Log.d(TAG, "[DEBUG] PERIODIC_UPDATE_CHANCE_INFO -PERIODIC_UPDATE_KEY: "+msg.getData().getString(PERIODIC_UPDATE_KEY));
                    mAccountsViewModel.searchChanceInfo(msg.getData().getString(PERIODIC_UPDATE_KEY));
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
        Queue<Integer> queue = mProcesses;
        if (queue == null || queue.size() <= 0) {
            return;
        }
        mHandler.sendEmptyMessage(queue.poll());
    }

    public void setRegisterProcess(int process) {
        Queue<Integer> queue = mProcesses;
        if (queue == null) {
            return;
        }
        queue.offer(process);
    }

    private void update() {
        ArrayList<Item> items = mUpdateProcesses;
        if (items == null) {
            return;

        }
        for (Item item:items) {
            Log.d(TAG, "[DEBUG] update - key: "+item.mKey+" type: "+item.mType);
            Bundle bundle = new Bundle();
            bundle.putString(PERIODIC_UPDATE_KEY, item.mKey);
            Message message = new Message();
            message.what = item.mType;
            message.setData(bundle);
            mHandler.sendMessage(message);
        }
    }

    public void setRegisterPeriodicUpdate(String key, int type) {
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

        mHandler.removeMessages(type);

//        mUpdateProcesses.removeIf(item -> {type -> (item.mType == type)});
        for (Iterator<Item> iterator = mUpdateProcesses.iterator(); iterator.hasNext(); ) {
            Item item = iterator.next();
            if (item.mType == type) {
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
