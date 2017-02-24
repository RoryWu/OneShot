package me.next.oneshot;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.StringDef;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import me.next.oneshot.utils.ImageUtils;
import me.next.oneshot.utils.SD;
import me.next.oneshot.utils.ShareUtils;
import me.next.oneshot.views.PaletteView;

/**
 * Created by NeXT on 17/2/10.
 */

public class PaletteActivity extends AppCompatActivity {

    FrameLayout flMain,flLoading;
    ImageView mImageView;
    PaletteView paletteView;

    int imageWidth;
    int imageHeight;

    boolean isSavingBitmap = false;
    @ACTION String action = ACTION_SAVE;

    private static final String ACTION_SHARE = "share";
    private static final String ACTION_SAVE = "save";
    @StringDef({ACTION_SHARE, ACTION_SAVE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ACTION {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palette);

        flMain = (FrameLayout) findViewById(R.id.fl_main);
        flLoading = (FrameLayout) findViewById(R.id.fl_loading);
        mImageView = (ImageView) findViewById(R.id.iv_image);
        paletteView = (PaletteView) findViewById(R.id.palette_view);

        String imagePath = getIntent().getStringExtra("imagePath");
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

                FrameLayout.LayoutParams flParams = ((FrameLayout.LayoutParams) flMain.getLayoutParams());
                params.width = imageWidth;
                params.height = imageHeight;
                flMain.setLayoutParams(flParams);

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
        if (isSavingBitmap) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.redo:
                paletteView.redo();
                break;
            case R.id.undo:
                paletteView.undo();
                break;
            case R.id.share:
                action = ACTION_SHARE;
                saveBitmap();
                break;
            case R.id.save:
                action = ACTION_SAVE;
                saveBitmap();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveBitmap() {
        new SaveImageTask().execute(flMain);
    }

    private class SaveImageTask extends AsyncTask<View, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isSavingBitmap = true;
            flLoading.setVisibility(View.VISIBLE);
            flLoading.animate().alpha(1.0f);
        }

        @Override
        protected String doInBackground(View... views) {
            View view = views[0];
            Bitmap bitmap = ImageUtils.getBitmapFromView(view);
            return SD.savePublicBitmap(getApplicationContext(), bitmap, "");
        }

        @Override
        protected void onPostExecute(final String imgPath) {
            super.onPostExecute(imgPath);
            isSavingBitmap = false;
            flLoading.animate().alpha(.0f).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    flLoading.setVisibility(View.GONE);
                }
            });
            if (action.equals(ACTION_SAVE)) {
                Snackbar.make(flMain, R.string.snack_share_image, Snackbar.LENGTH_LONG)
                        .setAction(R.string.snack_action_send, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ShareUtils.shareImg(PaletteActivity.this, imgPath);
                            }
                        })
                        .show();
            } else {
                ShareUtils.shareImg(PaletteActivity.this, imgPath);
            }
        }
    }

}
