package me.next.oneshot.utils;

import android.support.annotation.StringRes;
import android.widget.Toast;

import me.next.oneshot.OneShotApplication;

/**
 * Created by NeXT on 17/1/10.
 */

public class ToastUtils {

    public static void showToast(@StringRes int resId) {
        showToast(OneShotApplication.getInstance().getString(resId));
    }

    public static void showToast(String content) {
        Toast.makeText(OneShotApplication.getInstance(), content, Toast.LENGTH_SHORT).show();
    }

}
