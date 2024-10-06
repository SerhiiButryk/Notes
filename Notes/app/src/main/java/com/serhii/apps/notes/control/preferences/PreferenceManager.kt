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

    fun detailedLogsEnabled(context: Context): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(
            context.getString(R.string.preference_category_key_detail_logs),
            true
        )
    }

    fun setDetailedLogs(context: Context, value: Boolean) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit()
            .putBoolean(context.getString(R.string.preference_category_key_detail_logs), value)
            .apply()
    }

    fun getTimeout(context: Context): Long {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val timeDefault = 3*60*1000L
        return preferences.getLong(context.getString(R.string.preference_idle_lock_timeout_key), timeDefault)
    }

    fun setTimeout(context: Context, value: Long) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit()
            .putLong(context.getString(R.string.preference_idle_lock_timeout_key), value)
            .apply()
    }

}