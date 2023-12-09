/*
 * Copyright 2023. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.control.background_work

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.SystemClock
import com.serhii.core.log.Log

/**
 * Class which manages background work in application
 * TODO: Replace with coroutines
 */
object BackgroundWorkHandler {

    private const val TAG = "BackgroundWorkHandler"

    private val workItemsQueue: ArrayDeque<WorkItem> = ArrayDeque()

    fun putWork(workItem: WorkItem, context: Context) {
        Log.detail(TAG, "putWork, id = ${workItem.workItemId}")
        scheduleWork(workItem, context)
        workItemsQueue.add(workItem)
    }

    fun removeWork(workItemId: Int, context: Context) {
        Log.detail(TAG, "removeWork, id = $workItemId")
        cancelWork(workItemId, context)
        // Remove item for queue
        workItemsQueue.removeIf { it.workItemId == workItemId }
    }

    fun hasPendingWork(context: Context, workItemId: Int): Boolean {
        return getPendingWorkInfo(context, workItemId) != null
    }

    fun processWork(context: Context) {
        Log.detail(TAG, "processWork, sz = ${workItemsQueue.size} in")

        val completedWorkItems = mutableListOf<WorkItem>()

        for (item in workItemsQueue) {

            val delayTime = item.realTimeMillisInFuture
            val currentTime = SystemClock.elapsedRealtime()

            // Check if time is elapsed and if it's true the execute job
            if ((delayTime - currentTime) <= 0) {
                Log.detail(WorkItem.TAG, "processWork, time for = ${item.workItemId} is elapsed")
                item.onWorkStarted(context)

                completedWorkItems.add(item)
            } else {
                Log.detail(WorkItem.TAG, "processWork, time for = ${item.workItemId} is not elapsed")
            }
        }

        for (item in completedWorkItems) {
            // Remove completed work in queue
            workItemsQueue.remove(item)
        }

        Log.detail(TAG, "processWork, sz = ${workItemsQueue.size} out")
    }

    private fun scheduleWork(workItem: WorkItem, context: Context) {
        val scheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        val componentName = ComponentName(context, BackgroundWorkService::class.java)

        val info = JobInfo.Builder(workItem.workItemId, componentName)
            .setMinimumLatency(workItem.timeMillisInFuture)
            .build()

        val resultCode = scheduler.schedule(info)
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.detail(TAG, "scheduleWork(), success")
        } else {
            Log.error(TAG, "scheduleWork(), failed")
        }
    }

    private fun cancelWork(workItemId: Int, context: Context) {
        val scheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        scheduler.cancel(workItemId)
    }

    private fun getPendingWorkInfo(context: Context, id: Int): JobInfo? {
        val scheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        return scheduler.getPendingJob(id)
    }
}