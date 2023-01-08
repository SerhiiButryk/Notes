/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes

import android.app.Application
import android.os.Build
import com.serhii.apps.notes.common.AppDetails
import com.serhii.core.log.Log

/**
 * Application class which performs initialization of app components
 */
class Notes : Application() {

    override fun onCreate() {
        super.onCreate()
        // Init core library Log component
        Log.enableDetailedLogsForDebug()
        Log.tag = AppDetails.APP_LOG_TAG
    }
}