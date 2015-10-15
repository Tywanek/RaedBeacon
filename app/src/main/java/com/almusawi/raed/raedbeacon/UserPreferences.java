package com.almusawi.raed.raedbeacon;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by hqtmusicgroup1 on 11.05.2015.
 */
public abstract class UserPreferences {

    private static SharedPreferences getSharePreference(Context context) {
        return context.getSharedPreferences("UserPreferences", Activity.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditShare(Context context) {
        return getSharePreference(context).edit();
    }

    /**
     * USER PASSWORD
     */
    public static String getUserPassword(Context context) {
        return getSharePreference(context).getString("UserPreferences_Password", "");
    }

    public static void setUserPassword(Context context, String password) {
        getSharePreference(context).edit().putString("UserPreferences_Password", password).apply();
    }
}
