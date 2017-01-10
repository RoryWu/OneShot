package me.next.oneshot.services;

import android.os.Build;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;

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
    }

}
