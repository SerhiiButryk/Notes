/*
 * Copyright 2023. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.control.preferences

import android.content.Context
import com.serhii.apps.notes.ui.fragments.NoteViewFragment

object PreferenceManager {

    private const val preferenceFileName = "user_prefs"
    private const val NOTE_DISPLAY_MODE_KEY = "NOTE_DISPLAY_MODE_KEY"

    fun saveNoteDisplayMode(context: Context, mode: Int) {
        val preferences = context.getSharedPreferences(preferenceFileName, Context.MODE_PRIVATE)
        with (preferences.edit()) {
            putInt(NOTE_DISPLAY_MODE_KEY, mode)
            apply()
        }
    }

    fun getNoteDisplayMode(context: Context): Int {
        val preferences = context.getSharedPreferences(preferenceFileName, Context.MODE_PRIVATE)
        // Return default value if pref is not found
        return preferences.getInt(NOTE_DISPLAY_MODE_KEY, NoteViewFragment.DISPLAY_MODE_GRID)
    }

}