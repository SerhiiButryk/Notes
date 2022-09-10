/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes

import com.serhii.core.log.Log.Companion.info
import com.serhii.core.log.Log.Companion.tag
import com.serhii.core.log.Log.Companion.setDetailedLogs
import android.app.Application
import com.serhii.apps.notes.common.AppConstants

/**
 * Application class with app settings
 */
class Notes : Application() {

    override fun onCreate() {
        info(TAG, "onCreate(), IN")
        super.onCreate()
        setupLogLevel();
        info(TAG, "onCreate(), OUT")
    }

    private fun setupLogLevel() {
        // Initialize application configuration
        tag = AppConstants.APP_LOG_TAG
        // Enable detailed logs
        if (BuildConfig.DEBUG) {
            info(TAG, "setupLogLevel(), running debug build")
            setDetailedLogs(true)
        } else {
            info(TAG, "setupLogLevel(), running release build")
        }
    }

    companion object {
        private const val TAG = "Notes"
    }
}