/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.serhii.apps.notes.common.App.RUNTIME_LIBRARY
import com.serhii.apps.notes.control.AppForegroundListener
import com.serhii.apps.notes.control.idle_lock.IdleLockHandler
import com.serhii.core.log.Log
import com.serhii.core.utils.GoodUtils

/**
 * Class provides common behavior for all app activities
 */
open class AppBaseActivity : AppCompatActivity() {

    private var TAG_BASE = "AppBaseActivity-"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable secure screen content settings
        GoodUtils.enableUnsecureScreenProtection(this)
        // Do not add lifecycle observer for auth activity
        if (this !is AuthorizationActivity) {
            // Initialize lifecycle aware components
            lifecycle.addObserver(AppForegroundListener)
        }
        Log.info(TAG_BASE, "onCreate()")
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        IdleLockHandler.onUserInteraction(this)
    }

    override fun onResume() {
        super.onResume()
        IdleLockHandler.onActivityResumed(this)
        Log.info(TAG_BASE, "onResume()")
    }

    override fun onStop() {
        super.onStop()
        Log.info(TAG_BASE, "onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.info(TAG_BASE, "onDestroy()")
    }

    protected fun setLoggingTagForActivity(tag: String) {
        TAG_BASE += tag
    }

    init {
        System.loadLibrary(RUNTIME_LIBRARY)
        Log.info(TAG_BASE, "init()")
    }

    // A callback to an activity from a fragment
    interface NavigationCallback {
        fun onNavigateBack()
    }

}