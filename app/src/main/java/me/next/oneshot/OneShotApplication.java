package me.next.oneshot;

import android.app.Application;

/**
 * Created by NeXT on 17/1/10.
 */

public class OneShotApplication extends Application {

    private static class OneShotApplicationHolder {
        private static OneShotApplication oneShotApplication = new OneShotApplication();
    }

    public OneShotApplication() {
    }

    public static OneShotApplication getInstance() {
        return OneShotApplicationHolder.oneShotApplication;
    }
}
