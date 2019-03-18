package com.example.neno.rentacar;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by neno on 18.11.2015.
 */
public class Preferences {
    String PREFS_NAME = "Preferences";
    SharedPreferences sharedPreferencesInstance;

    public Preferences(Context context) {
        sharedPreferencesInstance = context.getSharedPreferences(PREFS_NAME, 0);
    }

    public void setString(String key, String value) {
        sharedPreferencesInstance.edit().putString(key, value).commit();
    }

    public void setInt(String key, Integer value) {
        sharedPreferencesInstance.edit().putInt(key, value).commit();
    }

    public String getString(String key) {
        return sharedPreferencesInstance.getString(key, "");
    }

    public Integer getInt(String key) {
        return sharedPreferencesInstance.getInt(key, 0);
    }
}
