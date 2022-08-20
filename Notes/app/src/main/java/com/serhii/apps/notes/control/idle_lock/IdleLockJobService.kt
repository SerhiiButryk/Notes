/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.control.idle_lock

import android.app.job.JobParameters
import android.app.job.JobService
import com.serhii.core.log.Log

class IdleLockJobService : JobService() {

    override fun onCreate() {
        Log.info(TAG, "onCreate() $this")
        super.onCreate()
    }

    override fun onDestroy() {
        Log.info(TAG, "onDestroy() $this")
        super.onDestroy()
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.info(TAG, "onStartJob()")
        // Call idle lock handler
        IdleLockHandler.onReceiveIdleLockEvent(this)
        // Work is completed
        return false;
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.info(TAG, "onStopJob()")

        // Do not reschedule job
        return false;
    }

    companion object {
        private const val TAG = "IdleLockJobService";
    }

}