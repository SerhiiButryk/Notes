/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.common

import com.serhii.apps.notes.BuildConfig
import kotlinx.coroutines.Dispatchers

// Global constants for this app
object App {
    const val RUNTIME_LIBRARY = "rabbit"
    const val VERSION_LIBRARY = BuildConfig.VERSION_NAME
    const val APP_LOG_TAG = "NotesApp"
    const val DEV_EMAIL = "sergeybutr@gmail.com"
    // For coroutines
    val BACKGROUND_DISPATCHER = Dispatchers.Default
    val UI_DISPATCHER = Dispatchers.Main
}