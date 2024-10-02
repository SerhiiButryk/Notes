/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.control.idle_lock

import android.content.Context
import android.content.Intent
import com.serhii.apps.notes.activities.AuthorizationActivity
import com.serhii.apps.notes.common.App
import com.serhii.apps.notes.control.AppForegroundListener
import com.serhii.apps.notes.control.preferences.PreferenceManager
import com.serhii.core.log.Log
import com.serhii.core.log.Log.Companion.detail
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Helper class for managing idle timeout in application
 */
object IdleLockHandler {

    private const val TAG = "IdleLockHandler"

    private val isInactivityTimeoutReceived = AtomicBoolean(false)

    private var lastJob: Job? = null

    fun onUserInteraction(context: Context) {
        detail(TAG, "onUserInteraction()")
        startLockTimeout(context, PreferenceManager.getTimeout(context))
    }

    fun onActivityResumed(context: Context) {
        detail(TAG, "onActivityResumed()")
        // Check if timeout is received and start a job if not
        if (!hasIdleTimeout(context)) {
            startLockTimeout(context, PreferenceManager.getTimeout(context))
        }
    }

    fun restartTimer(context: Context, time: Long) {
        detail(TAG, "restartTimer()")
        startLockTimeout(context, time)
    }

    // TODO: Revisit global scope
    @OptIn(DelicateCoroutinesApi::class)
    private fun startLockTimeout(context: Context, time: Long) {

        if (context is AuthorizationActivity) {
            Log.detail(TAG, "startLockTimeout(), ignore for this activity")
            // Stop timer job
            lastJob?.cancel()
            return
        }

        lastJob?.cancel()

        lastJob = GlobalScope.launch(App.BACKGROUND_DISPATCHER) {
            Log.detail(TAG, "coroutine: started")
            delay(time)
            Log.detail(TAG, "coroutine: time elapsed")
            onTimeout(context)
            Log.detail(TAG, "coroutine: finished")
        }
    }

    private suspend fun onTimeout(context: Context) {
        Log.detail(TAG, "onTimeout(), received inactivity timeout, time: " + Date(System.currentTimeMillis()))
        isInactivityTimeoutReceived.set(true)
        if (AppForegroundListener.isInForeground()) {
            Log.detail(TAG, "onTimeout(), in foreground, start auth activity")
            withContext(App.UI_DISPATCHER) {
                startAuthActivity(context, Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
    }

    private fun hasIdleTimeout(context: Context): Boolean {

        if (context is AuthorizationActivity) {
            Log.detail(TAG, "hasIdleTimeout(), ignore for this activity")
            isInactivityTimeoutReceived.set(false)
            return false
        }

        if (isInactivityTimeoutReceived.get()) {
            Log.detail(TAG,"hasIdleTimeout(), timeout received, start auth activity")
            startAuthActivity(context, Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            return true
        }

        return false
    }

    private fun startAuthActivity(context: Context, flags: Int) {
        val startActivityIntent = Intent(context, AuthorizationActivity::class.java)
        startActivityIntent.flags = flags
        context.startActivity(startActivityIntent)
        // Reset flag
        isInactivityTimeoutReceived.set(false)
    }
}