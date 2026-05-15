package com.notes.os.impl

import api.Platform
import api.utils.Log
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

abstract class PlatformLog : Log {

    @OptIn(ExperimentalAtomicApi::class)
    protected var isDebug = AtomicBoolean(false)

    @OptIn(ExperimentalAtomicApi::class)
    override fun setDebug(isDebug: Boolean) {
        this.isDebug.store(isDebug)
        Platform().logger.logi("setDebug: isDebug = $isDebug")
    }

    @OptIn(ExperimentalAtomicApi::class)
    override fun logd(message: String) {
        if (isDebug.load()) {
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