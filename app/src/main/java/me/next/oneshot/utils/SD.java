package me.next.oneshot.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by NeXT on 17/1/10.
 */

public class SD {

    private static final String FOLDER_NAME = "OneShot";
    private static final String LOG_TAG = "OneShot";

    /* Checks if external storage is available for read and write */
    public static boolean sdCardIsAvail() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * 应用目录，com.xxx.xxx 目录下
     * @param context
     * @param albumName
     * @return
     */
    public static File getAlbumStorageDir(Context context, String albumName) {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }

    /**
     * 获取公共目录
     * @param albumName
     * @return
     */
    public static File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }

    public static String savePublicBitmap(Context context, Bitmap bitmap, String fileName) {
        File imgDir = getAlbumStorageDir(FOLDER_NAME);
        String filePath = createFile(bitmap, fileName, imgDir);
        sendBroadCast(context, imgDir);
        return filePath;
    }

    /**
     * 保存到项目目录下
     * @param context
     * @param bitmap
     * @param fileName
     * @return
     */
    public static String saveBitmap(Context context, Bitmap bitmap, String fileName) {
        File imgDir = getAlbumStorageDir(context, FOLDER_NAME);
        String filePath = createFile(bitmap, fileName, imgDir);
        sendBroadCast(context, imgDir);
        return filePath;
    }

    /**
     * 发送图片库更新后的广播
     * @param context
     * @param imgDir
     */
    private static void sendBroadCast(Context context, File imgDir) {
        Uri fileUri = Uri.fromFile(imgDir);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent mediaScanIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(fileUri);
            context.sendBroadcast(mediaScanIntent);
        } else {
            context.sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_MOUNTED,
                    fileUri));
        }
    }

    /**
     * Bitmap 转 File
     * @param bitmap
     * @param fileName
     * @param imgDir
     * @return
     */
    @NonNull
    private static String createFile(Bitmap bitmap, String fileName, File imgDir) {
        if (TextUtils.isEmpty(fileName)) {
            fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
        }
        File file = new File(imgDir, fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return file.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
