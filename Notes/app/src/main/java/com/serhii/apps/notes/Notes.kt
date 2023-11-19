/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes

import android.app.Application
import com.serhii.apps.notes.common.AppDetails
import com.serhii.apps.notes.control.preferences.PreferenceManager
import com.serhii.core.log.Log

/**
 * Application class. This is the place fot initialization of app components
 */
class Notes : Application() {
    override fun onCreate() {
        super.onCreate()
        // Setup global app settings
        Log.init()
        Log.tag = AppDetails.APP_LOG_TAG
        val enabled = PreferenceManager.isDetailLogsEnabled(this)
        Log.enableDetailedLogs(enabled)
        Log.setVersionCode(BuildConfig.VERSION_NAME)
    }
}