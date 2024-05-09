/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.serhii.apps.notes.R
import com.serhii.apps.notes.common.App
import com.serhii.apps.notes.common.App.RUNTIME_LIBRARY
import com.serhii.apps.notes.control.AppForegroundListener
import com.serhii.apps.notes.control.idle_lock.IdleLockHandler
import com.serhii.core.log.Log
import com.serhii.core.utils.GoodUtils
import kotlinx.coroutines.withContext

/**
 * Class provides common behavior for all activities
 */
open class AppBaseActivity : AppCompatActivity() {

    private var TAG_BASE = "AppBaseActivity-"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable secure screen content settings
        GoodUtils.enableUnsecureScreenProtection(this)
        // Initialize lifecycle aware components
        lifecycle.addObserver(AppForegroundListener)
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

    suspend fun showStatusMessage(result: Boolean) {
        withContext(App.UI_DISPATCHER) {
            if (result) {
                GoodUtils.showToast(baseContext, R.string.result_success)
            } else {
                GoodUtils.showToast(baseContext, R.string.result_failed)
            }
        }
    }

    fun showMessage(id: Int) {
        GoodUtils.showToast(baseContext, id)
    }

    init {
        System.loadLibrary(RUNTIME_LIBRARY)
        Log.info(TAG_BASE, "init()")
    }

    // A callback from a fragment
    interface NavigationCallback {
        fun onNavigateBack()
    }

}