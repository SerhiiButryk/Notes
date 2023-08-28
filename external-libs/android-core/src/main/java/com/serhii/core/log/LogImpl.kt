/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.log

import android.os.Build
import android.util.Log
import com.serhii.core.CoreEngine.loadNativeLibrary

internal object LogImpl : ILog {

    // Default values
    private var TAG: String = ""
    private const val DELIMITER = " "

    private const val ERROR = 1
    private const val INFO = 2
    private const val DETAIL = 3

    private var VERSION_CODE = ""

    private var isFirstCall = false

    override fun info(tag: String, message: String) {
        logInternal(tag, message, INFO)
    }

    override fun detail(tag: String, message: String) {
        if (isDetailedLogsEnabled()) {
            logInternal(tag, message, DETAIL)
        }
    }

    override fun error(tag: String, message: String) {
        logInternal(tag, message, ERROR)
    }

    private fun logInternal(tag: String, message:  String, level: Int) {

        val predicate: String = when (level) {
            ERROR, INFO -> DELIMITER + tag + DELIMITER
            DETAIL -> "DETAIL$DELIMITER$tag$DELIMITER"
            else -> ""
        }

        val messageFormatted = predicate + message;

        when (level) {
            ERROR -> {
                Log.e(TAG, messageFormatted)
            }
            INFO, DETAIL -> {
                Log.i(TAG, messageFormatted)
            }
        }
    }

    override fun setVersionCode(versionCode: String) {
        VERSION_CODE = versionCode

        // Log debug information once
        if (!isFirstCall) {
            Log.i("", getDebugInformation())
            isFirstCall = true
        }
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
        try {
            // Can throw exception if running on release build
            _enableDetailLogForDebug()
        } catch (e: ClassNotFoundException) {
        }
    }

    private fun isDetailedLogsEnabled(): Boolean {
        return _getDetailLog()
    }

    private fun getDebugInformation(): String {
        return "Device: " + Build.DEVICE + " " + Build.MANUFACTURER + " " + Build.MODEL + " " + Build.BRAND + "\n" +
               "Android OS: " + Build.VERSION.CODENAME + " " + Build.VERSION.SDK_INT + "\n" +
               "Version code: " + VERSION_CODE + "\n"
    }

    init {
        loadNativeLibrary()
    }

    private external fun _setTag(tag: String)
    private external fun _setDetailLog(enable: Boolean)
    private external fun _getDetailLog(): Boolean
    private external fun _enableDetailLogForDebug()
}