package com.serhii.apps.notes;

import android.app.Application;

import com.serhii.apps.notes.common.AppConstants;
import com.serhii.apps.notes.control.idle_lock.InactivityManager;
import com.serhii.core.log.Log;

public class Notes extends Application {

    private static final String TAG = "Notes";

    @Override
    public void onCreate() {
        Log.info(TAG, "onCreate(), IN");
        super.onCreate();
        // Initialize application configuration
        Log.setTag(AppConstants.APP_LOG_TAG);
        // Enable detailed logs
        if (BuildConfig.DEBUG) {
            Log.info(TAG, "onCreate(), running debug build");
            Log.setDetailedLogs(true);
        } else {
            Log.info(TAG, "onCreate(), running release build");
        }
        Log.info(TAG, "onCreate(), OUT");
    }
}
