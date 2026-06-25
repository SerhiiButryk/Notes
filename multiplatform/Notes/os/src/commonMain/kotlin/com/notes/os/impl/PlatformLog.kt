package com.notes.os.impl

import api.Platform
import api.utils.Log
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

/**
 * Platform base implementation of a log utility
 */
abstract class PlatformLog : Log {

    @OptIn(ExperimentalAtomicApi::class)
    var _isDebug = AtomicBoolean(false)

    @OptIn(ExperimentalAtomicApi::class)
    override val isDebug: Boolean
        get() = _isDebug.load()

    @OptIn(ExperimentalAtomicApi::class)
    override fun setDebug(isDebug: Boolean) {
        this._isDebug.store(isDebug)
        Platform().logger.logi("setDebug: isDebug = $isDebug")
    }

    @OptIn(ExperimentalAtomicApi::class)
    override fun logd(message: String) {
        if (_isDebug.load()) {
            logDebug(message)
        }
    }

    open fun logDebug(message: String) {}

    override fun close() {
    }

    override fun createCustomComposeTracer(): Any {
        throw RuntimeException("Not supported")
    }

}