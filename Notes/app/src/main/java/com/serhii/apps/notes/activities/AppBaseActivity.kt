/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.serhii.apps.notes.common.AppDetails.RUNTIME_LIBRARY
import com.serhii.apps.notes.control.AppForegroundListener
import com.serhii.apps.notes.control.idle_lock.IdleLockHandler
import com.serhii.apps.notes.control.idle_lock.InactivityManager
import com.serhii.core.log.Log
import com.serhii.core.utils.GoodUtils

/**
 * Class provides common behavior for all app activities
 */
open class AppBaseActivity : AppCompatActivity() {

    private val TAG = "NotesViewActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable unsecured screen content settings
        //GoodUtils.enableUnsecureScreenProtection(this)
        // Initialize lifecycle aware components
        lifecycle.addObserver(AppForegroundListener)
        Log.info(TAG, "onCreate() out")
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        InactivityManager.onUserInteraction(this)
        Log.info(TAG, "onUserInteraction() out")
    }

    override fun onResume() {
        super.onResume()
        Log.info(TAG, "onResume() int")
        // Trigger idle timeout
        if (!IdleLockHandler.handleInactivityTimeoutReceived(this)) {
            // If timeout is not triggered then schedule new alarm
            InactivityManager.scheduleAlarm(this)
        }
        Log.info(TAG, "onResume() out")
    }

    init {
        System.loadLibrary(RUNTIME_LIBRARY)
        Log.info(TAG, "init() finished")
    }

}