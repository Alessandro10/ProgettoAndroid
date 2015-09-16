package com.example.tonino.login;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.ref.SoftReference;

/**
 * Created by Daniele on 24/07/2015.
 */
public class SharedPreferencesHandler {

    protected SharedPreferencesHandler() {}

    public static SharedPreferencesHandler getInstance() {
        if (instance == null) {
            instance = new SoftReference<>(new SharedPreferencesHandler());
        }
        if (instance.get() == null) {
            instance = new SoftReference<>(new SharedPreferencesHandler());
        }
        return instance.get();
    }

    protected static SoftReference<SharedPreferencesHandler> instance;

    protected SharedPreferences sharedPref;

    protected SharedPreferences.Editor editor;

    protected void initializeReadIfNeeded(Context context) {
        if (sharedPref == null) {
            String key = context.getString(R.string.shared_preferences_key);
            sharedPref = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        }
    }

    protected void initializeWriteIfNeeded(Context context) {
        initializeReadIfNeeded(context);
        if (editor == null) {
            editor = sharedPref.edit();
        }
    }

    public String get(Context context, int key, String defValue) {
        return get(context, context.getString(key), defValue);
    }

    public String get(Context context, String key, String defValue) {
        initializeReadIfNeeded(context);
        return sharedPref.getString(key, defValue);
    }

    public void put(Context context, int key, String value) {
        put(context, context.getString(key), value);
    }

    public void put(Context context, String key, String value) {
        initializeWriteIfNeeded(context);
        editor.putString(key, value);
    }

    public boolean get(Context context, int key, boolean defValue) {
        return get(context, context.getString(key), defValue);
    }

    public boolean get(Context context, String key, boolean defValue) {
        initializeReadIfNeeded(context);
        return sharedPref.getBoolean(key, defValue);
    }

    public void put(Context context, int key, boolean value) {
        put(context, context.getString(key), value);
    }

    public void put(Context context, String key, boolean value) {
        initializeWriteIfNeeded(context);
        editor.putBoolean(key, value);
    }

    public void commit() {
        editor.commit();
    }

}
