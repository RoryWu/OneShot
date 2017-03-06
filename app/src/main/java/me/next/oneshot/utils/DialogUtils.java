package me.next.oneshot.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import me.next.oneshot.R;
import me.next.oneshot.WeChatPayActivity;
import moe.feng.alipay.zerosdk.AlipayZeroSdk;

/**
 * Created by NeXT on 17/3/3.
 */

public class DialogUtils {

    public static void showPayDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.dialog_pay_title)
                .setMessage(R.string.dialog_pay_message)
                .setPositiveButton(R.string.dialog_button_alibaba, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (AlipayZeroSdk.hasInstalledAlipayClient(activity)) {
                            AlipayZeroSdk.startAlipayClient(activity, "aex05180taoh73dkvgdhk06");
                        } else {
                            Toast.makeText(activity, R.string.toast_alipay_client_not_found, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_button_wechat, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(activity, WeChatPayActivity.class);
                        activity.startActivity(intent);
                    }
                })
                .setNeutralButton(R.string.dialog_button_goodbye, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(activity, R.string.toast_byebye, Toast.LENGTH_LONG).show();
                    }
                });
        builder.create().show();
    }
}
