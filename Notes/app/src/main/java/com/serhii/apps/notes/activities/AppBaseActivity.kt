/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.activities

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.serhii.apps.notes.common.App.RUNTIME_LIBRARY
import com.serhii.apps.notes.control.AppForegroundListener
import com.serhii.apps.notes.control.idle_lock.IdleLockHandler
import com.serhii.core.BuildConfig
import com.serhii.core.log.Log

/**
 * Class provides common behavior for all activities
 */
open class AppBaseActivity : AppCompatActivity() {

    private var TAG = "AppBaseActivity-"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Log.info(TAG, "onCreate()")
        // Enable secure screen content settings
        enableUnsecureScreenProtection(this)
        // Initialize lifecycle aware components
        lifecycle.addObserver(AppForegroundListener)
    }

    private fun enableUnsecureScreenProtection(activity: Activity): Boolean {
        // Enable for release build
        if (!BuildConfig.DEBUG) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
            return true
        }
        return false
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        Log.info(TAG, "onUserInteraction()")
        IdleLockHandler.onUserInteraction(this)
    }

    override fun onResume() {
        super.onResume()
        Log.info(TAG, "onResume()")
        IdleLockHandler.onActivityResumed(this)
    }

    override fun onStop() {
        super.onStop()
        Log.info(TAG, "onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.info(TAG, "onDestroy()")
    }

    protected fun setLoggingTagForActivity(tag: String) {
        TAG += tag
    }

    init {
        System.loadLibrary(RUNTIME_LIBRARY)
        Log.info(TAG, "init()")
    }

}