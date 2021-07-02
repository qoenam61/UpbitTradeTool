package com.example.upbitautotrade;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public final class UpBitLogInPreferences {
    private final static String TAG = "UpBitLogInPreferences";

    private static SharedPreferences mSharedPreferences = null;

    public static void create(Context context) {
        String masterKeyAlias = null;
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            mSharedPreferences = EncryptedSharedPreferences.create(
                    "upbit_login_info",
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setStoredKey(Context context, String key, String data) {
        SharedPreferences pref = mSharedPreferences;
        if (pref == null) {
            Log.d(TAG, "[DEBUG]setStoredKey: null");
            return;
        }
        SharedPreferences.Editor editor = pref.edit();
        // use the shared preferences and editor as you normally would
        editor.putString(key, data);
    }

    public static String getStoredKey(Context context, String key) {
        SharedPreferences pref = mSharedPreferences;
        if (pref == null) {
            Log.d(TAG, "[DEBUG]getStoredKey: null");
            return null;
        }
        return pref.getString(key, "null");
    }
}
