package com.serhii.apps.notes;

import android.app.Application;

import com.serhii.apps.notes.common.AppConstants;
import com.serhii.core.CoreEngine;
import com.serhii.core.log.Log;
import com.serhii.core.log.LogImpl;
import com.serhii.core.security.Hash;
import com.serhii.core.security.impl.crypto.Result;

public class Notes extends Application {

    private static final String TAG = "Notes";

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize application configuration
        Log.setTag(AppConstants.APP_LOG_TAG);

        Log.info(TAG, "onCreate(), in");

        // Enable detailed logs
        if (BuildConfig.DEBUG) {
            Log.info(TAG, "onCreate(), running debug build");
            Log.setDetailedLogs(true);
        }

        Log.info(TAG, "onCreate(), out");
    }
}
