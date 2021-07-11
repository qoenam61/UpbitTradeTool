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
import java.util.ConcurrentModificationException;
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


    private long mPeriodicTimer = 100;
    private final Queue<Item> mProcesses;
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
                    mAccountsViewModel.searchChanceInfo(msg.getData().getString(PERIODIC_UPDATE_KEY + msg.what));
                    break;
                case PERIODIC_UPDATE_TICKER_INFO:
                    mAccountsViewModel.searchTickerInfo(msg.getData().getString(PERIODIC_UPDATE_KEY + msg.what));
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
            while (true) {
                process();
                update();
            }
        });
    }

    private void process() {
        if (mProcesses == null || mProcesses.isEmpty()) {
            return;
        }
        Item item = mProcesses.poll();
        Bundle bundle = new Bundle();
        bundle.putString(PERIODIC_UPDATE_KEY, item.key);
        Message message = new Message();
        message.what = item.type;
        message.setData(bundle);
        mHandler.sendMessage(message);
    }

    public void setRegisterProcess(Item item) {
        if (mProcesses == null) {
            return;
        }
        mProcesses.offer(item);
    }

    private void update() {
        ArrayList<Item> processes = mUpdateProcesses;
        if (processes == null || processes.isEmpty()) {
            return;

        }
        try {
            Iterator<Item> iterator = processes.iterator();
            while (iterator.hasNext()) {
                Item item = iterator.next();
                Bundle bundle = new Bundle();
                bundle.putString(PERIODIC_UPDATE_KEY + item.type, item.key);
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
        }

    }

    public void registerPeriodicUpdate(String key, int type) {
        if (mUpdateProcesses == null) {
            return;

        }
        Item item = new Item(key, type);
        mUpdateProcesses.add(item);
    }

    public void removePeriodicUpdate(int type) {
        if (mUpdateProcesses == null) {
            return;

        }
        for (Iterator<Item> iterator = mUpdateProcesses.iterator(); iterator.hasNext(); ) {
            Item item = iterator.next();
            if (item.type == type) {
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
            if (item.key == key) {
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
}
