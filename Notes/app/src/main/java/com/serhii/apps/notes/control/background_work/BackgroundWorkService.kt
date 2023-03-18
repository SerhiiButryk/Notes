/*
 * Copyright 2023. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.control.background_work

import android.app.job.JobParameters
import android.app.job.JobService
import com.serhii.core.log.Log

/**
 * Class which executes background work in application
 */
class BackgroundWorkService : JobService() {

    override fun onStartJob(p0: JobParameters?): Boolean {
        Log.info(TAG, "onStartJob, in")

        BackgroundWorkHandler.processWork(this)

        // Work is completed when the function is returned
        return false
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        Log.info(TAG, "onStopJob, in")
        // Do not reschedule this work
        return false
    }

    companion object {
        const val TAG = "BackgroundWorkService"
    }
}