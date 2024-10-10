/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.common

import com.serhii.apps.notes.BuildConfig
import kotlinx.coroutines.Dispatchers

// Application global constants
object App {
    const val RUNTIME_LIBRARY = "rabbit"
    const val VERSION = BuildConfig.VERSION_NAME
    const val APP_LOG_TAG = "NotesApp"
    const val DEV_EMAIL = "sergeybutr@gmail.com"
    // Background execution settings
    val BACKGROUND_DISPATCHER = Dispatchers.Default
    val UI_DISPATCHER = Dispatchers.Main
}