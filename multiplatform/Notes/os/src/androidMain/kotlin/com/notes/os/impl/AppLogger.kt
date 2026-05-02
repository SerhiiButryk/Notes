package com.notes.os.impl

import api.utils.Log
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

class AppLogger : Log {

    @OptIn(ExperimentalAtomicApi::class)
    private var isDebug = AtomicBoolean(false)

    override fun logi(message: String) {
        android.util.Log.i("Notes", message)
    }

    override fun loge(message: String) {
        android.util.Log.e("Notes", message)
    }

    @OptIn(ExperimentalAtomicApi::class)
    override fun logd(message: String) {
        if (isDebug.load()) {
            android.util.Log.d("Notes", message)
        }
    }

    @OptIn(ExperimentalAtomicApi::class)
    override fun setDebug(isDebug: Boolean) {
        this.isDebug.store(isDebug)
    }
}