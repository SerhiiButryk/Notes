/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.control.idle_lock

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.preference.PreferenceManager
import com.serhii.apps.notes.R
import com.serhii.apps.notes.activities.AuthorizationActivity
import com.serhii.apps.notes.control.AppForegroundListener
import com.serhii.apps.notes.control.background_work.BackgroundWorkHandler
import com.serhii.apps.notes.control.background_work.WorkId
import com.serhii.apps.notes.control.background_work.WorkItem
import com.serhii.core.log.Log.Companion.detail
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Helper class for managing idle timeout in application
 */
object IdleLockHandler {

    private const val TAG = "IdleLockHandler"

    private val isInactivityTimeoutReceived = AtomicBoolean(false)

    fun onUserInteraction(context: Context) {
        startLockTimeout(context)
    }

    fun onActivityResumed(context: Context) {
        if (context is AuthorizationActivity) {
            detail(TAG, "onActivityResumed(), stop timer")
            // Stop inactivity timer\
            cancelLockTimeout(context)
        } else {
            detail(TAG, "onActivityResumed(), start timer")
            // Check if inactivity timeout is received
            checkInactivityTimeout(context)
            // Start lock timer
            startLockTimeout(context)
        }
    }

    fun forceRestartTimer(context: Context, newTimeMillis: Long) {
        detail(TAG, "forceRestartTimer()")
        cancelLockTimeout(context)
        startLockTimeout(context, newTimeMillis)
    }

    private fun startLockTimeout(context: Context, timeMillis: Long = 0) {
        detail(TAG, "startLockTimeout(), in")

        if (context is AuthorizationActivity) {
            detail(TAG, "startLockTimeout(), ignore for auth activity")
            return
        }

        if (BackgroundWorkHandler.hasPendingWork(context, WorkId.IDLE_LOCK_WORK_ID)) {
            detail(TAG, "startLockTimeout(), has pending job")
            return
        }

        detail(TAG, "startLockTimeout(), schedule work")

        val time = if (timeMillis == 0L) getTimeout(context) else timeMillis

        detail(TAG, "getTimeout(), time: $time")

        val workItem = WorkItem(WorkId.IDLE_LOCK_WORK_ID, time, { cxt, _ ->
            onLockEventReceived(cxt)
        }, null, null)

        BackgroundWorkHandler.putWork(workItem, context)

        detail(TAG, "startLockTimeout(), out")
    }

    private fun cancelLockTimeout(context: Context) {
        detail(TAG, "cancelLockTimeout()")
        BackgroundWorkHandler.removeWork(WorkId.IDLE_LOCK_WORK_ID, context)
    }

    private fun onLockEventReceived(context: Context) {
        detail(TAG, "onLockEventReceived(), received inactivity timeout, time: " + Date(System.currentTimeMillis()))
        isInactivityTimeoutReceived.set(true)
        if (AppForegroundListener.isInForeground()) {
            detail(TAG, "onLockEventReceived(), in foreground, start auth activity")
            startAuthActivity(context, Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    private fun checkInactivityTimeout(context: Context): Boolean {

        if (context is AuthorizationActivity) {
            detail(TAG, "checkInactivityTimeout(), ignore for auth activity")
            isInactivityTimeoutReceived.set(false)
            return false
        }

        if (isInactivityTimeoutReceived.get()) {
            detail(TAG,"checkInactivityTimeout(), time out received, start auth activity")
            startAuthActivity(context, Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            return true
        }
        return false
    }

    private fun getTimeout(context: Context) : Long {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val timeDefault = context.getString(R.string.preference_idle_lock_time_default)
        val time = sharedPreferences.getString(context.getString(R.string.preference_idle_lock_timeout_key), timeDefault)

        val timeoutTimeMillis = time?.toLong() ?: timeDefault.toLong()

        return timeoutTimeMillis
    }

    private fun startAuthActivity(context: Context, flags: Int) {
        val startActivityIntent = Intent(context, AuthorizationActivity::class.java)
        startActivityIntent.flags = flags
        context.startActivity(startActivityIntent)
        // Reset flag
        isInactivityTimeoutReceived.set(false)
    }
}