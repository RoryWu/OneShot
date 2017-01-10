package me.next.oneshot.utils;

import android.util.Log;

import me.next.oneshot.BuildConfig;

/**
 * Created by NeXT on 17/1/10.
 */

public class LogUtils {

    public static void e(String content) {
        Log.e(BuildConfig.APPLICATION_ID, content);
    }
}
