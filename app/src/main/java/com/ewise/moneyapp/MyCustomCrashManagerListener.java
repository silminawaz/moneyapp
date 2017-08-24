package com.ewise.moneyapp;

import net.hockeyapp.android.CrashManagerListener;

/**
 * Created by rahul on 24/8/17.
 */

public class MyCustomCrashManagerListener extends CrashManagerListener {
    @Override
    public boolean shouldAutoUploadCrashes() {
        return true;
    }
}
