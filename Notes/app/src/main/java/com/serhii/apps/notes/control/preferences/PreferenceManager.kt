/*
 * Copyright 2023. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.control.preferences

import android.content.Context
import androidx.preference.PreferenceManager
import com.serhii.apps.notes.R

/**
 * Helper class for basis User's settings/preferences
 */
object PreferenceManager {

    fun detailLogsEnabled(context: Context): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(context.getString(R.string.preference_category_key_detail_logs), true)
    }

    fun getTimeout(context: Context) : Long {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val timeDefault = context.getString(R.string.preference_idle_lock_time_default)
        val time = sharedPreferences.getString(context.getString(R.string.preference_idle_lock_timeout_key), timeDefault)

        val timeoutTimeMillis = time?.toLong() ?: timeDefault.toLong()

        return timeoutTimeMillis
    }

}