package com.icedcap.itbookfinder.help;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by shuqi on 16-4-4.
 */
public class SharedPreferencesHelper {
    private static final String SHARED_PREFERENCES_NAME = "settings";
    private static final String HOME_PAGE_LAYOUT = "homepageLayout";
    private static final String AUTO_CACHE_IN_WIFI = "autoCache";


    public static void setHomepageLayout(Context c, String isLinear) {
        final SharedPreferences sp = getSharedPreferences(c);
        sp.edit().putString(HOME_PAGE_LAYOUT, isLinear).apply();
    }

    public static String getHomepageLayout(Context c) {
        final SharedPreferences sharedPreferences = getSharedPreferences(c);
        return sharedPreferences.getString(HOME_PAGE_LAYOUT, "1");
    }

    public static void setAutoCache(Context c, boolean autoCache) {
        getSharedPreferences(c).edit().putBoolean(AUTO_CACHE_IN_WIFI, autoCache).apply();
    }

    public static boolean getAutoCache(Context c) {
        return getSharedPreferences(c).getBoolean(AUTO_CACHE_IN_WIFI, true);
    }

    private static SharedPreferences getSharedPreferences(Context c) {
        return c.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

}
