package me.next.oneshot.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * Created by NeXT on 17/2/24.
 */

public class ShareUtils {

    public static void shareImg(Activity activity, String imagePath) {
        Uri imgUri = Uri.fromFile(new File(imagePath));
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(Intent.EXTRA_STREAM, imgUri);
        activity.startActivity(Intent.createChooser(intent, "分享到..."));
    }


}
