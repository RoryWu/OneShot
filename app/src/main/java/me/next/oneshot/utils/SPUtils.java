package me.next.oneshot.utils;

import android.content.Context;
import android.content.SharedPreferences;

import me.next.oneshot.OneShotApplication;
import me.next.oneshot.R;

/**
 * Created by NeXT on 17/2/21.
 */

public class SPUtils {

    private static SharedPreferences mSharedPreferences;

    private static SharedPreferences getSharedPreference() {
        if (mSharedPreferences != null) {
            return mSharedPreferences;
        }
        return mSharedPreferences = OneShotApplication.getInstance().getSharedPreferences(
                OneShotApplication.getInstance().getString(R.string.preference_file_name), Context.MODE_PRIVATE);
    }

    public static void updateNotification(boolean b) {
        getSharedPreference().edit().putBoolean("notificationCenter", b).apply();
    }

    public static boolean showNotificationCenter() {
        return getSharedPreference().getBoolean("notificationCenter", true);
    }

}
