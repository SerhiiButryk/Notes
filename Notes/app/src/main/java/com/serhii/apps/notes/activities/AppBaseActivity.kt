/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.serhii.apps.notes.common.App.RUNTIME_LIBRARY
import com.serhii.apps.notes.control.AppForegroundListener
import com.serhii.apps.notes.control.idle_lock.IdleLockHandler
import com.serhii.core.log.Log
import com.serhii.core.utils.GoodUtils

/**
 * Class provides common behavior for all activities
 */
open class AppBaseActivity : AppCompatActivity() {

    private var TAG_BASE = "AppBaseActivity-"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Log.info(TAG_BASE, "onCreate()")
        // Enable secure screen content settings
        GoodUtils.enableUnsecureScreenProtection(this)
        // Initialize lifecycle aware components
        lifecycle.addObserver(AppForegroundListener)
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        Log.info(TAG_BASE, "onUserInteraction()")
        IdleLockHandler.onUserInteraction(this)
    }

    override fun onResume() {
        super.onResume()
        Log.info(TAG_BASE, "onResume()")
        IdleLockHandler.onActivityResumed(this)
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

}