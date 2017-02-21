package me.next.oneshot;

import android.app.Application;

/**
 * Created by NeXT on 17/1/10.
 */

public class OneShotApplication extends Application {

    private static OneShotApplication oneShotApplication;

    public static OneShotApplication getInstance() {
        return oneShotApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        oneShotApplication = this;
    }

}
