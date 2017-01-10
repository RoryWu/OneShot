package me.next.oneshot;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;

import java.nio.ByteBuffer;

import me.next.oneshot.utils.CapturePhotoUtils;
import me.next.oneshot.utils.LogUtils;
import me.next.oneshot.utils.ScreenUtils;
import me.next.oneshot.utils.ToastUtils;

/**
 * see: http://binwaheed.blogspot.jp/2015/03/how-to-correctly-take-screenshot-using.html
 */
public class ScreenShotActivity extends AppCompatActivity {

    private static final int MEDIA_PROJECT_REQUEST_CODE = 1026;
    MediaProjectionManager mMediaProjectionManager;
    MediaProjection mProjection;
    ImageReader mImageReader;
    Handler handler;

    private int screenWidth = 0;
    private int screenHeight = 0;
    private int screenDensity = 0;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_shot);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ToastUtils.showToast(R.string.target_sdk_must_greater_than_lollipop);
            finish();
            return;
        }

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        screenDensity = metrics.densityDpi;
        screenWidth = ScreenUtils.getScreenWidth(ScreenShotActivity.this);
        screenHeight = ScreenUtils.getScreenHeight(ScreenShotActivity.this);

        LogUtils.e("screenWidth : " + screenWidth);
        LogUtils.e("screenHeight : " + screenHeight);
        LogUtils.e("screenDensity : " + screenDensity);

        handler = new Handler();

        mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), MEDIA_PROJECT_REQUEST_CODE);

        findViewById(R.id.bt_take_screen_shot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mProjection.createVirtualDisplay("screen-mirror", screenWidth, screenHeight, screenDensity,
                                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mImageReader.getSurface(), null, null);
                    }
                }, 10);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Image image = mImageReader.acquireLatestImage();

                        int width = image.getWidth();                   //Nexus6 imageWidth : 1440 * 4 = 5760
                        int height = image.getHeight();

                        //http://ranlic.iteye.com/blog/1313735
                        final Image.Plane[] planes = image.getPlanes();
                        final ByteBuffer buffer = planes[0].getBuffer();
                        int pixelStride = planes[0].getPixelStride();   //像素跨度  Nexus6 = 4
                        int rowStride = planes[0].getRowStride();       //行跨度    Nexus6 = 6656

                        int rowPadding = rowStride - pixelStride * width;   //896
                        // create bitmap
                        Bitmap bmp = Bitmap.createBitmap(width + rowPadding / pixelStride,
                                height, Bitmap.Config.ARGB_8888); //Bitmap.Config.RGB_565
                        bmp.copyPixelsFromBuffer(buffer);
                        image.close();
                        //截取黑边
                        if (width != screenWidth || rowPadding != 0) {
                            int[] pixel = new int[width + rowPadding / pixelStride];
                            bmp.getPixels(pixel, 0, width + rowPadding / pixelStride, 0, 0, width + rowPadding / pixelStride, 1);
                            int leftPadding = 0;
                            int rightPadding = width + rowPadding / pixelStride;
                            //从最左到右判断是否有颜色填充 --->
                            for (int i = 0; i < pixel.length; i++) {
                                if (pixel[i] != 0) {
                                    leftPadding = i;
                                    break;
                                }
                            }
                            //从最右到左判断是否有颜色填充 <---
                            for (int i = pixel.length - 1; i >= 0; i--) {
                                if (pixel[i] != 0) {
                                    rightPadding = i;
                                    break;
                                }
                            }
                            width = Math.min(width, screenHeight);
                            if (rightPadding - leftPadding > width) {
                                rightPadding = width;
                            }
                            bmp = Bitmap.createBitmap(bmp, leftPadding, 0, rightPadding - leftPadding, height);
                        }

                        String imagePath = CapturePhotoUtils.insertImage(
                                getContentResolver(),
                                bmp, "title", "desc...");
                        /*
                        File localFile = new File(SD.getProjectImageDir(OneShotApplication.getInstance()), "temp.png");
                        String fileName=localFile.getAbsolutePath();
                        LogUtils.e("文件路径: " + fileName);
                        try {
                            if (!localFile.exists()) {
                                localFile.createNewFile();
                            }
                            FileOutputStream fileOutputStream = new FileOutputStream(localFile);
                            if (fileOutputStream != null) {
                                bmp.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                                fileOutputStream.flush();
                                fileOutputStream.close();
                            }
                        } catch (IOException e) {
                        }
                        */

//                        ImageView imageView = (ImageView) findViewById(R.id.iv_img);
//                        imageView.setImageBitmap(bmp);

                        bmp.recycle();
                    }
                }, 100);
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MEDIA_PROJECT_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                mProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
                mImageReader = ImageReader.newInstance(screenWidth,
                        screenHeight, PixelFormat.RGBA_8888, 2); //ImageFormat.RGB_565 cast error
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void stopMediaProjection() {
        if (mProjection != null) {
            mProjection.stop();
            mProjection = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMediaProjection();
    }

}
