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
import com.serhii.core.log.Log
import com.serhii.core.utils.GoodUtils

/**
 * Class provides common behavior for all app activities
 */
open class AppBaseActivity : AppCompatActivity() {

    protected var TAG_BASE = "AppBaseActivity-"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable secure screen content settings
        GoodUtils.enableUnsecureScreenProtection(this)
        // Do not add lifecycle observer for auth activity
        if (this !is AuthorizationActivity) {
            // Initialize lifecycle aware components
            lifecycle.addObserver(AppForegroundListener)
        }
        Log.info(TAG_BASE, "onCreate() out")
    }

    override fun onUserInteraction() {
        Log.info(TAG_BASE, "onUserInteraction()")
        super.onUserInteraction()
        IdleLockHandler.onUserInteraction(this)
    }

    override fun onResume() {
        Log.info(TAG_BASE, "onResume() in")
        super.onResume()
        IdleLockHandler.onActivityResumed(this)
        Log.info(TAG_BASE, "onResume() out")
    }

    override fun onStop() {
        super.onStop()
        Log.info(TAG_BASE, "onStop() out")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.info(TAG_BASE, "onDestroy() out")
    }

    init {
        System.loadLibrary(RUNTIME_LIBRARY)
        Log.info(TAG_BASE, "init() finished")
    }

}