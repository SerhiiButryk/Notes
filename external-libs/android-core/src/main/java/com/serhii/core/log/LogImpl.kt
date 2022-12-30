/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.log

import android.util.Log
import com.serhii.core.CoreEngine.loadNativeLibrary

internal object LogImpl : ILog {

    // Default values
    private var TAG: String = ""
    private const val DELIMITER = " "

    override fun info(tag: String, message: String) {
        val predicate = DELIMITER + tag + DELIMITER
        Log.i(TAG, predicate + message)
    }

    override fun detail(tag: String, message: String) {
        if (getDetailedLogsEnabled()) {
            val predicate = "DETAIL$DELIMITER$tag$DELIMITER"
            Log.i(TAG, predicate + message)
        }
    }

    override fun error(tag: String, message: String) {
        val predicate = DELIMITER + tag + DELIMITER
        Log.e(TAG, predicate + message)
    }

    override var tag: String
        get() = TAG
        set(tag) {
            TAG = tag
            _setTag(tag)
        }

    fun setDetailedLogs(isEnabled: Boolean) {
        _setDetailLog(isEnabled)
    }

    fun setDetailedLogsForDebug() {
        _enableDetailLogForDebug()
    }

    fun getDetailedLogsEnabled(): Boolean {
        return _getDetailLog()
    }

    init {
        loadNativeLibrary()
    }

    private external fun _setTag(tag: String)
    private external fun _setDetailLog(enable: Boolean)
    private external fun _getDetailLog(): Boolean
    private external fun _enableDetailLogForDebug()
}