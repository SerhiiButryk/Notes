package com.serhii.apps.notes;

import android.app.Application;

import com.serhii.apps.notes.common.AppConstants;
import com.serhii.core.log.Log;

public class Notes extends Application {

    private static final String TAG = "Notes";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.info(TAG, "onCreate(), in");

        // Initialize application configuration
        Log.setTag(AppConstants.APP_LOG_TAG);

        // Enable detailed logs
        if (BuildConfig.DEBUG) {
            Log.info(TAG, "onCreate(), this is Debug build");
            Log.setDetailedLogs(true);
        }

        Log.info(TAG, "onCreate(), out");
    }
}
