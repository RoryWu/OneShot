package me.next.oneshot.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by NeXT on 17/1/10.
 */

public class SD {

    private static final String FOLDER_NAME = "OneShot";

    /**
     * 返回app的图片目录路径
     */
    public static String getProjectImageDir(Context context) {
        return getProjectDir(context) + File.separatorChar + "images";
    }

    /**
     * 返回app的目录路径
     */
    public static String getProjectDir(Context context) {
        String dir = null;
        if (sdCardIsAvail()) {
            dir = Environment.getExternalStorageDirectory().getPath() + File.separatorChar + FOLDER_NAME;
        } else {
            dir = context.getFilesDir().getAbsolutePath() + File.separatorChar + FOLDER_NAME;
        }
        File f = new File(dir);
        if (!f.exists()) {
            f.mkdirs();
        }
        return dir;
    }

    /**
     * 判断SD卡是否可用
     */
    public static boolean sdCardIsAvail() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }



}
