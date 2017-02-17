package me.next.oneshot;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import me.next.oneshot.utils.CapturePhotoUtils;
import me.next.oneshot.utils.ImageUtils;
import me.next.oneshot.views.PaletteView;

/**
 * Created by NeXT on 17/2/10.
 */

public class PaletteActivity extends AppCompatActivity {

    FrameLayout flMain;
    ImageView mImageView;
    PaletteView paletteView;

    int imageWidth;
    int imageHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palette);

        flMain = (FrameLayout) findViewById(R.id.fl_main);
        mImageView = (ImageView) findViewById(R.id.iv_image);
        paletteView = (PaletteView) findViewById(R.id.palette_view);

        Uri imageUri = Uri.parse(getIntent().getStringExtra("imagePath"));
        String imagePath = ImageUtils.getRealPathFromUri(getApplicationContext(), imageUri);
        mImageView.setImageURI(Uri.fromFile(new File(imagePath)));

        ViewTreeObserver vto = mImageView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                mImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                imageWidth = mImageView.getMeasuredWidth();
                imageHeight = mImageView.getMeasuredHeight();

                if (imageWidth < 500 && imageHeight < 500) {
                    imageWidth = imageWidth * 2;
                    imageHeight = imageHeight * 2;
                    mImageView.getLayoutParams().width = imageWidth;
                    mImageView.getLayoutParams().height = imageHeight;
                    mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }

                Log.e("OneShot", "imageWidth : " + imageWidth);
                Log.e("OneShot", "imageHeight : " + imageHeight);

                paletteView.getLayoutParams().width = imageWidth;
                paletteView.getLayoutParams().height = imageHeight;
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_palette, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.redo:
                paletteView.redo();
                break;
            case R.id.undo:
                paletteView.undo();
                break;
            case R.id.save:
                Bitmap bitmap = ImageUtils.getBitmapFromView(flMain);
                String imagePath = CapturePhotoUtils.insertImage(
                        getContentResolver(),
                        bitmap, "title", "desc...");
                Toast.makeText(getApplicationContext(), "图片已保存到: " + imagePath, Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
