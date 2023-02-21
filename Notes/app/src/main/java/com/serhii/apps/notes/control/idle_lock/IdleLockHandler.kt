/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.control.idle_lock

import android.content.Context
import android.content.Intent
import com.serhii.apps.notes.activities.AuthorizationActivity
import com.serhii.apps.notes.control.AppForegroundListener
import com.serhii.core.log.Log.Companion.detail
import java.util.*

/**
 * Helper class for starting authentication activity when idle time out is reached
 */
object IdleLockHandler {

    private const val TAG = "IdleLockHandler"
    private var isInactivityTimeoutReceived = false

    fun onReceiveIdleLockEvent(context: Context) {
        detail(TAG, "onReceiveIdleLockEvent(), received inactivity timeout, time: " + Date(System.currentTimeMillis()))
        isInactivityTimeoutReceived = true
        if (AppForegroundListener.isInForeground()) {
            detail(TAG, "onReceiveIdleLockEvent(), in foreground, start auth activity")
            startActivity(context, Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    @JvmStatic
    fun handleInactivityTimeoutReceived(context: Context): Boolean {
        if (isInactivityTimeoutReceived) {
            detail(TAG,"handleInactivityTimeoutReceived(), time out received, start auth activity")
            startActivity(context, Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            return true
        }
        return false
    }

    private fun startActivity(context: Context, flags: Int) {
        val startActivityIntent = Intent(context, AuthorizationActivity::class.java)
        startActivityIntent.flags = flags
        context.startActivity(startActivityIntent)
        // Reset flag
        isInactivityTimeoutReceived = false
    }
}