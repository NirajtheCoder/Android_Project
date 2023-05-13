package com.example.esales.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MyPreferences {
    private SharedPreferences preferences;

    public MyPreferences(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setPreferenceString(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public String getPreferenceString(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    public void setPreferenceInt(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getPreferenceInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    public void setPreferenceLong(String key, long value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public long getPreferenceLong(String key, long defaultValue) {
        return preferences.getLong(key, defaultValue);
    }

    public void setPreferenceFloat(String key, float value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public float getPreferenceFloat(String key, float defaultValue) {
        return preferences.getFloat(key, defaultValue);
    }

    public void setPreferenceBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getPreferenceBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }
}