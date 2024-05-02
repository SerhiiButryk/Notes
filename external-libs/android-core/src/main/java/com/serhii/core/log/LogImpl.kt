/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.log

import android.os.Build
import android.util.Log
import com.serhii.core.CoreEngine.loadNativeLibrary

internal object LogImpl : ILog {

    private var TAG: String = ""
    private const val DELIMITER = " "

    private const val ERROR = 1
    private const val INFO = 2
    private const val DETAIL = 3

    private var VERSION_CODE = ""

    private var versionCodeInfoSet = false
    private var firstCall = false

    fun init() {
        setDetailedLogsIfDebug()
    }

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

    // TODO: Should delegate calls to native
    private fun logInternal(tag: String, message:  String, level: Int) {

        if (versionCodeInfoSet && !firstCall) {
            // Log this info once
            Log.i(TAG, getDebugInformation())
            firstCall = true
        }

        val predicate: String = tag + DELIMITER
        val messageFormatted = predicate + message

        var appTag = TAG
        if (level == DETAIL) {
            appTag += "-DETAIL"
        }

        when (level) {
            ERROR -> {
                Log.e(appTag, messageFormatted)
            }
            INFO -> {
                Log.i(appTag, messageFormatted)
            }
            DETAIL -> {
                Log.v(appTag, messageFormatted)
            }
        }
    }

    override fun setVersionCode(versionCode: String) {
        VERSION_CODE = versionCode
    }

    override var tag: String
        get() = TAG
        set(tag) {
            TAG = tag
            _setTag(tag)
        }

    fun setDetailedLogs(enabled: Boolean) {
        _setDetailLog(enabled)
    }

    private fun setDetailedLogsIfDebug() {
        _enableDetailLogIfDebug()
    }

    private fun isDetailedLogsEnabled(): Boolean {
        return _isDetailLogEnabled()
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
    private external fun _isDetailLogEnabled(): Boolean
    private external fun _enableDetailLogIfDebug(): Boolean
}