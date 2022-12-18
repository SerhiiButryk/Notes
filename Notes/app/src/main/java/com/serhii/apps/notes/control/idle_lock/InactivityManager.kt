/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.control.idle_lock

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import androidx.preference.PreferenceManager
import com.serhii.apps.notes.R
import com.serhii.apps.notes.control.idle_lock.IdleLockJobService
import com.serhii.core.log.Log.Companion.detail
import com.serhii.core.log.Log.Companion.error
import com.serhii.core.log.Log.Companion.info

/**
 * Helper class which schedule and reschedule job for idle timeout
 */
object InactivityManager {

    private const val IDLE_LOCK_JOB_SERVICE_ID = 100

    private var timeoutTimeMillis = 0
    private const val TAG = "InactivityManager"

    @JvmStatic
    fun updateTimeout(context: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val timeDefault = context.getString(R.string.preference_idle_lock_time_default)
        val time = sharedPreferences.getString(context.getString(R.string.preference_idle_lock_timeout_key), timeDefault)
        timeoutTimeMillis = time?.toInt() ?: 0
        detail(TAG, "updateTimeout(), retrieved time: $timeoutTimeMillis")
    }

    // Schedule job service
    @JvmStatic
    fun scheduleAlarm(context: Context) {
        detail(TAG, "scheduleAlarm(), inactivity alarm is scheduled TIME: " + System.currentTimeMillis())

        // Get idle timeout value 
        updateTimeout(context)

        val componentName = ComponentName(context, IdleLockJobService::class.java)
        val info = JobInfo.Builder(IDLE_LOCK_JOB_SERVICE_ID, componentName)
            .setMinimumLatency(timeoutTimeMillis.toLong())
            .build()

        val scheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        val resultCode = scheduler.schedule(info)
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            info(TAG, "scheduleAlarm(), success")
        } else {
            error(TAG, "scheduleAlarm(), failed")
        }

    }

    // Stop job service
    @JvmStatic
    fun cancelAlarm(context: Context) {
        detail(TAG, "cancelAlarm() in")
        val scheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        scheduler.cancel(IDLE_LOCK_JOB_SERVICE_ID)
    }

    @JvmStatic
    fun onUserInteraction(context: Context) {
        detail(TAG, "onUserInteraction()")
        // Reschedule
        scheduleAlarm(context)
    }

}