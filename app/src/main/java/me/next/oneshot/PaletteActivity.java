package me.next.oneshot;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
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
        final File imageFile = new File(imagePath);
        mImageView.setImageURI(Uri.fromFile(imageFile));

        BitmapFactory.Options dimensions = new BitmapFactory.Options();
        dimensions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, dimensions);
        final int height = dimensions.outHeight;
        final int width = dimensions.outWidth;

        ViewTreeObserver vto = mImageView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                mImageView.getViewTreeObserver().removeOnPreDrawListener(this);

                int rootViewHeight = flMain.getMeasuredHeight();

                if (height > rootViewHeight) {
                    mImageView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                    mImageView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    mImageView.setAdjustViewBounds(true);
                    float ratio = ((float) rootViewHeight / (float) height);
                    imageWidth = (int) (ratio * width);
                } else {
                    imageWidth = mImageView.getMeasuredWidth();
                }
                imageHeight = mImageView.getMeasuredHeight();

                FrameLayout.LayoutParams params = ((FrameLayout.LayoutParams) paletteView.getLayoutParams());
                params.width = imageWidth;
                params.height = imageHeight;
                paletteView.setLayoutParams(params);

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
