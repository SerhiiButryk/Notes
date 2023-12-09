/*
 * Copyright 2023. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.control.background_work

import android.content.Context
import android.os.Handler
import android.os.SystemClock
import com.serhii.core.log.Log

/**
 * A piece of work to execute
 */
class WorkItem(
    val workItemId: Int,
    val timeMillisInFuture: Long,
    private val workCallback: (Context, WorkItem) -> Unit,
    private val completionListener: CompletionListener? = null,
    private val handler: Handler? = null) {

    val realTimeMillisInFuture: Long = SystemClock.elapsedRealtime() + timeMillisInFuture

    // Object for storing additional input data for this work item
    var extraData: Any? = null

    fun onWorkStarted(context: Context) {
        Log.detail(TAG, "onWorkStarted, id = $workItemId in")
        // Run work
        workCallback(context, this)
        // Notify client if can
        completionListener?.let {
            // If handler != null
            handler?.post{
                completionListener.notifyWorkCompleted(workItemId)
            }
                // If handler == null
                ?: completionListener.notifyWorkCompleted(workItemId)
        }
        Log.detail(TAG, "onWorkStarted, id = $workItemId in")
    }

    companion object {
        const val TAG = "WorkItem"
    }
}