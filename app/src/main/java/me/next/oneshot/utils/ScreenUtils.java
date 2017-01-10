package me.next.oneshot.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * Created by NeXT on 17/1/10.
 */

public class ScreenUtils {

    public static int getScreenWidth(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        return metrics.widthPixels;
    }


    public static int getScreenHeight(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

}
