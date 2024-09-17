/*
 * Copyright 2023. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.control.preferences

import android.content.Context
import com.serhii.apps.notes.R

/**
 * Helper class for basis User's settings/preferences
 */
object PreferenceManager {

    fun isDetailLogsEnabled(context: Context): Boolean {
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(context.getString(R.string.preference_category_key_detail_logs), true)
    }

}