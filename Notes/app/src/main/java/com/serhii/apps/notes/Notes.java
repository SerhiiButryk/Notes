package com.serhii.apps.notes;

import android.app.Application;

import com.serhii.core.log.Log;
import com.serhii.apps.notes.common.AppConstants;

public class Notes extends Application {

    private static final String TAG = Notes.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize here application configuration
        Log.setTag(AppConstants.APP_LOG_TAG);

        Log.info(TAG, "onCreate(), application is created");
    }
}
