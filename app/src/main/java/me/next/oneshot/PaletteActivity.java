package me.next.oneshot;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import me.next.oneshot.utils.ImageUtils;
import me.next.oneshot.utils.SD;
import me.next.oneshot.utils.ShareUtils;
import me.next.oneshot.views.PaletteView;
import petrov.kristiyan.colorpicker.ColorPicker;

/**
 * Created by NeXT on 17/2/10.
 */

public class PaletteActivity extends AppCompatActivity {

    private static final float PAINT_WIDTH_SMALL = 8F;
    private static final float PAINT_WIDTH_MEDIUM = 16F;
    private static final float PAINT_WIDTH_LARGE = 28F;


    FrameLayout flMain,flLoading;
    ImageView mImageView;
    PaletteView paletteView;

    FloatingActionMenu floatingActionsMenu;
    FloatingActionButton fabPaintSmall, fabPaintMedium, fabPaintLarge, fabPaintColor;

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

        initFab();

//        final String imagePath = "/storage/emulated/0/Android/data/me.next.oneshot/files/Pictures/OneShot/temp";
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
                int rootViewWidth = flMain.getMeasuredWidth();

                if (height < rootViewHeight && width < rootViewWidth) { //图片宽高都没有超过 parent view 宽高
                    float ratioHeight = ((float) rootViewHeight / (float) height);
                    float ratioWidth = ((float) rootViewWidth / (float) width);
                    if (ratioHeight < ratioWidth) {
                        imageWidth = (int) (width * ratioHeight);
                        mImageView.getLayoutParams().width = imageWidth;
                    } else {
                        imageWidth = rootViewWidth;
                    }
                } else if (height > rootViewHeight) {
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

    private void initFab() {

        floatingActionsMenu = (FloatingActionMenu) findViewById(R.id.fam_main);
        fabPaintSmall = (FloatingActionButton) findViewById(R.id.fab_paint_small);
        fabPaintMedium = (FloatingActionButton) findViewById(R.id.fab_paint_medium);
        fabPaintLarge = (FloatingActionButton) findViewById(R.id.fab_paint_large);
        fabPaintColor = (FloatingActionButton) findViewById(R.id.fab_paint_color);

        floatingActionsMenu.getMenuIconView();
        floatingActionsMenu.setClosedOnTouchOutside(true);
        floatingActionsMenu.setIconAnimated(false);
        floatingActionsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingActionsMenu.close(true);
            }
        });

        fabPaintSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paletteView.setPaintStrokeWidth(PAINT_WIDTH_SMALL);
                floatingActionsMenu.close(true);
            }
        });

        fabPaintMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paletteView.setPaintStrokeWidth(PAINT_WIDTH_MEDIUM);
                floatingActionsMenu.close(true);
            }
        });

        fabPaintLarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paletteView.setPaintStrokeWidth(PAINT_WIDTH_LARGE);
                floatingActionsMenu.close(true);
            }
        });

        fabPaintColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingActionsMenu.close(true);
                final ColorPicker colorPicker = new ColorPicker(PaletteActivity.this);
                colorPicker.setColumns(4);
                colorPicker.setColors(R.array.colors);
                colorPicker.setColorButtonDrawable(R.drawable.round_button);
                colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
                    @Override
                    public void setOnFastChooseColorListener(int position, int color) {
                        paletteView.setPaintStrokeColor(color);

                        updateFabImgColor(fabPaintColor, color);
                        updateFabImgColor(fabPaintSmall, color);
                        updateFabImgColor(fabPaintMedium, color);
                        updateFabImgColor(fabPaintLarge, color);
                        floatingActionsMenu.getIcon().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                        if (color == getResources().getColor(R.color.black)) {
                            floatingActionsMenu.setMenuButtonColorNormal(getResources().getColor(R.color.colorAccent));
                        } else {
                            floatingActionsMenu.setMenuButtonColorNormal(getResources().getColor(R.color.colorPrimaryDark));
                        }
                    }

                    @Override
                    public void onCancel(){
                    }
                }).setDefaultColorButton(paletteView.getPaintStrokeColor()).setColumns(5).show();
            }
        });
    }

    private void updateFabImgColor(FloatingActionButton fabPaint, int color) {
        if (color == getResources().getColor(R.color.black)) {
            fabPaint.setColorNormal(getResources().getColor(R.color.colorAccent));
        } else {
            fabPaint.setColorNormal(getResources().getColor(R.color.colorPrimaryDark));
        }
        Drawable drawable = fabPaint.getIconDrawable();
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
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
