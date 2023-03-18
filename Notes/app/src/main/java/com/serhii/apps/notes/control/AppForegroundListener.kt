/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.control

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.serhii.core.log.Log.Companion.info
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Listener for foreground / background transitions.
 */
object AppForegroundListener : LifecycleObserver {

    private const val TAG = "AppForegroundListener"
    private val isInForeground = AtomicBoolean(false)

    fun isInForeground() = isInForeground.get()

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onForegroundEntered() {
        info(TAG, "onForegroundEntered()")
        isInForeground.set(true)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onForegroundExited() {
        info(TAG, "onForegroundExited()")
        isInForeground.set(false)
    }

}