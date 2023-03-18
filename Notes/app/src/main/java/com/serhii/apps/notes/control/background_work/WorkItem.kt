/*
 * Copyright 2023. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.control.background_work

import android.content.Context
import android.os.Handler
import com.serhii.core.log.Log

/**
 * A piece of work to execute
 */
class WorkItem(
    val workItemId: Int,
    val timeMillisInFuture: Long,
    private val workCallback: (Context) -> Unit,
    private val completionListener: CompletionListener? = null,
    private val handler: Handler? = null) {

    fun onWorkStarted(context: Context) {
        Log.info(TAG, "onWorkStarted, id = $workItemId in")
        // Run work
        workCallback(context)
        // Notify client if can
        handler?.let {
            h ->
            if (completionListener != null) {
                h.post {
                    completionListener.notifyWorkCompleted(workItemId)
                }
            }
        }
        Log.info(TAG, "onWorkStarted, id = $workItemId in")
    }

    companion object {
        const val TAG = "WorkItem"
    }
}