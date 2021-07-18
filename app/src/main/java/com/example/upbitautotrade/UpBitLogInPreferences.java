package com.example.upbitautotrade;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public final class UpBitLogInPreferences {
    private final static String TAG = "UpBitLogInPreferences";

    public final static String ACCESS_KEY = "access_key";
    public final static String SECRET_KEY = "secret_key";

    public UpBitLogInPreferences() {
        throw new IllegalStateException("인스턴스 생성 금지");
    }

    public static void setStoredKey(Context context, String key, String data) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit()
                .putString(key, data)
                .apply();
    }

    public static String getStoredKey(Context context, String key) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(key, "");
    }
}
