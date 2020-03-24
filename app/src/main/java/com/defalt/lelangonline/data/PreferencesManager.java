package com.defalt.lelangonline.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {

    private static PreferencesManager _instance;

    private SharedPreferences sharedPref;

    public static void instance(Context context) {
        if (_instance == null) {
            _instance = new PreferencesManager();
            _instance.configSessionUtils(context);
        }
    }

    public static PreferencesManager instance() {
        return _instance;
    }

    private void configSessionUtils(Context context) {
        sharedPref = context.getSharedPreferences("AppPreferences", Activity.MODE_PRIVATE);
    }

    public void storeValueString(String key, String value) {
        sharedPref.edit()
                .putString(key, value)
                .apply();
    }

    public String fetchValueString(String key) {
        return sharedPref.getString(key, null);
    }

    public boolean clear() {
        return sharedPref.edit()
                .clear()
                .commit();
    }
}
