package com.notes.os.impl

import api.data.AppSettings
import api.utils.Log

/**
 * Platform base implementation of a log utility
 */
abstract class PlatformLog : Log {

    override fun logd(message: String) {
        if (AppSettings.isDebugEnabled) {
            logDebug(message)
        }
    }

    open fun logDebug(message: String) {}

    override fun close() {
    }

    override fun createCustomComposeTracer(): Any = throw RuntimeException("Not supported")
}
