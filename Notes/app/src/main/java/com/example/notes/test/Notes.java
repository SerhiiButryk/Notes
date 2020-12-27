package com.example.notes.test;

import android.app.Application;

import com.example.core.log.Log;
import com.example.notes.test.common.AppConstants;

public class Notes extends Application {

    public static final String TAG = Notes.class.getSimpleName();

    @Override
    public void onCreate() {
        // Initialize here application configuration
        Log.setTag(AppConstants.APP_LOG_TAG);

        super.onCreate();

        Log.info(TAG, "application is created");
    }
}
