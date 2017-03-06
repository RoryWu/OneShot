package me.next.oneshot;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import me.next.oneshot.utils.SD;
import me.next.oneshot.utils.ShareUtils;

public class WeChatPayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_we_chat_pay);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                Toast.makeText(getApplicationContext(), R.string.toast_share_to_wechat, Toast.LENGTH_SHORT).show();
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.wechat_pay);
                String imagePath = SD.saveBitmap(getApplicationContext(), bitmap, "wechat_pay.jpg");
                ShareUtils.shareImg(WeChatPayActivity.this, imagePath);
                break;
        }
        return true;
    }
}
