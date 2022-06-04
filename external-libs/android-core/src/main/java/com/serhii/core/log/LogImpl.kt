/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.log

import android.util.Log
import com.serhii.core.CoreEngine.loadNativeLibrary

internal object LogImpl : ILog {

    private var TAG: String = ""
    private const val DELIMITER = " "
    private var ENABLED_DETAILED_LOGS = false

    override fun info(tag: String, message: String) {
        Log.i(TAG + DELIMITER + tag + DELIMITER, message)
    }

    override fun detail(tag: String, message: String) {
        if (ENABLED_DETAILED_LOGS) {
            Log.i("$TAG DETAIL $tag$DELIMITER", message)
        }
    }

    override fun error(tag: String, message: String) {
        Log.e(TAG + DELIMITER + tag + DELIMITER, message)
    }

    override var tag: String
        get() = TAG
        set(tag) {
            TAG = tag
            _setTag(tag)
        }

    fun setDetailedLogs(isEnabled: Boolean) {
        ENABLED_DETAILED_LOGS = isEnabled
    }

    init {
        loadNativeLibrary()
    }

    private external fun _setTag(tag: String?)
}