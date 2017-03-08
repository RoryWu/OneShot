package me.next.oneshot.services;

import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;

import me.next.oneshot.ScreenShotActivity;

/**
 * Created by NeXT on 17/1/10.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class OneShotTileService extends TileService {

    @Override
    public void onTileAdded() {
        super.onTileAdded();
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onClick() {
        super.onClick();

        // 收起系统 Notification Center
        // see: http://stackoverflow.com/questions/15568754/how-to-close-the-status-bar-notification-panel-after-notification-button-click
//        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//        getApplicationContext().sendBroadcast(it);

        Intent intent = new Intent(getApplicationContext(), ScreenShotActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityAndCollapse(intent);
    }

}
