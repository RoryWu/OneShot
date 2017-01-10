package me.next.oneshot;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import me.next.oneshot.utils.PermissionUtils;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        detectPermission();

        findViewById(R.id.bt_screen_shot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ScreenShotActivity.class);
                startActivity(intent);
            }
        });
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
