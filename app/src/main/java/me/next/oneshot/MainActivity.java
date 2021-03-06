package me.next.oneshot;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import me.next.oneshot.utils.DialogUtils;
import me.next.oneshot.utils.PermissionUtils;
import me.next.oneshot.utils.SPUtils;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    private Switch mSwitch;
    private RelativeLayout tvPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        detectPermission();

        tvPay = (RelativeLayout) findViewById(R.id.rl_pay);
        tvPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showPayDialog(MainActivity.this);
            }
        });

        mSwitch = (Switch) findViewById(R.id.switch_notification_center);

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updateNotificationCenter(b);
                SPUtils.updateNotification(b);
            }
        });

        findViewById(R.id.bt_screen_shot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(MainActivity.this, ScreenShotActivity.class);
                startActivity(intent);
            }
        });

        boolean showNotificationCenter = SPUtils.showNotificationCenter();
        mSwitch.setChecked(showNotificationCenter);
        updateNotificationCenter(showNotificationCenter);

    }

    private void updateNotificationCenter(boolean showNotificationCenter) {
        if (showNotificationCenter) {
            showNotificationCenter();
        } else {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(1024);
        }
    }

    private void showNotificationCenter() {

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_crop_white)
                        .setLargeIcon(bm)
                        .setContentTitle("OneShot")
                        .setContentText("点击进行屏幕截图");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBuilder.setPriority(Notification.PRIORITY_MIN); // notification icon won't show up on the statusbar
        }

        Intent resultIntent = new Intent(this, ScreenShotActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, 0);
        mBuilder.setContentIntent(resultPendingIntent);

        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(1024, notification);
    }

    private void detectPermission() {
        if (PackageManager.PERMISSION_GRANTED !=
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat
                    .requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // 如果请求被取消， grantResults 列表总是为空
            if (PermissionUtils.verifyPermissions(grantResults)) {
//                Toast.makeText(MainActivity.this, "ヽ(✿ﾟ▽ﾟ)ノ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "_(┐「ε:)_ 不给权限我走了...", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
