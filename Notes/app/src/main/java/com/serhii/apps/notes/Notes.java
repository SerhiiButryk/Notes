package com.serhii.apps.notes;

import android.app.Application;

import com.serhii.apps.notes.common.AppConstants;
import com.serhii.core.log.Log;

public class Notes extends Application {

    private static final String TAG = Notes.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize here application configuration
        Log.setTag(AppConstants.APP_LOG_TAG);

        // Enable detailed logs
        if (BuildConfig.DEBUG) {
            Log.setDetailedLogs(true);
        }

        Log.info(TAG, "onCreate(), application is created");
    }
}
