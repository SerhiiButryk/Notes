/*
 * Copyright 2023. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.fragments

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.serhii.apps.notes.R
import com.serhii.apps.notes.common.AppDetails
import com.serhii.apps.notes.control.NativeBridge
import com.serhii.apps.notes.control.backup.BackupManager.openBackUpFile
import com.serhii.apps.notes.control.backup.BackupManager.openDirectoryChooserForBackup
import com.serhii.apps.notes.control.backup.BackupManager.openDirectoryChooserForExtractData
import com.serhii.apps.notes.control.idle_lock.InactivityManager.updateTimeout
import com.serhii.apps.notes.database.UserNotesDatabase.recordsCount
import com.serhii.apps.notes.ui.dialogs.DialogHelper
import com.serhii.apps.notes.ui.dialogs.DialogHelper.showAlertDialog
import com.serhii.apps.notes.ui.dialogs.DialogHelper.showChangePasswordDialog
import com.serhii.core.log.Log.Companion.error
import com.serhii.core.log.Log.Companion.info
import com.serhii.core.utils.GoodUtils.Companion.formatString

/**
 * Fragment with application settings
 */
class SettingsFragment : PreferenceFragmentCompat() {

    private var loginAttempts: Preference? = null

    private val nativeBridge = NativeBridge()

    override fun onCreatePreferences(bundle: Bundle, s: String) {
        setPreferencesFromResource(R.xml.preferences, s)
        initLayout()
    }

    private fun initLayout() {

        val changePassword = findPreference<Preference>(getString(R.string.preference_change_password_key))

        Preference.OnPreferenceClickListener {
            showChangePasswordDialog(requireActivity())
            true
        }.also { changePassword?.onPreferenceClickListener = it }

        loginAttempts = findPreference(getString(R.string.preference_login_limit_key))

        loginAttempts?.summary = formatString(
            getString(R.string.preference_login_limit_summary),
            nativeBridge.getLockLimit(requireContext())
        )

        loginAttempts!!.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->

                val selectedValue = newValue.toString().toInt()

                info(TAG, "onPreferenceChange(), selected new login limit: $selectedValue")

                loginAttempts?.summary =
                    formatString(getString(R.string.preference_login_limit_summary), selectedValue)

                nativeBridge.limitLeft = selectedValue

                true
            }

        val version = findPreference<Preference>(getString(R.string.preference_about_version_key))
        version?.summary = AppDetails.VERSION_LIBRARY

        val idleLockTimeOut =
            findPreference<Preference>(getString(R.string.preference_idle_lock_timeout_key))

        idleLockTimeOut?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->
                val selectedTime = newValue.toString().toInt()
                info(TAG, "onPreferenceChange(), selected idle lock time $selectedTime")
                updateTimeout(requireContext())
                true
            }

        val extractNotes = findPreference<Preference>(getString(R.string.preference_extract_key))
        extractNotes!!.onPreferenceClickListener = object : Preference.OnPreferenceClickListener {
            override fun onPreferenceClick(preference: Preference): Boolean {
                info(TAG, "onPreferenceClick()")
                if (!isDataAvailable) {
                    return true
                }
                openDirectoryChooserForExtractData(
                    activity!!
                )
                return true
            }
        }

        val backupNotes = findPreference<Preference>(getString(R.string.preference_backup_key))
        backupNotes!!.onPreferenceClickListener = object : Preference.OnPreferenceClickListener {
            override fun onPreferenceClick(preference: Preference): Boolean {
                if (!isDataAvailable) {
                    return true
                }
                openDirectoryChooserForBackup(
                    activity!!
                )
                return false
            }
        }

        val unlockNote = findPreference<Preference>(getString(R.string.preference_unlock_note_key))
        unlockNote!!.summary = nativeBridge.unlockKey

        val restoreNotes = findPreference<Preference>(getString(R.string.preference_restore_note_key))
        restoreNotes!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            openBackUpFile(requireActivity())
            true
        }
    }

    // Check if there is data to extract
    private val isDataAvailable: Boolean
        get() {
            // Check if there is data to extract
            if (recordsCount == 0) {
                showAlertDialog(DialogHelper.ALERT_DIALOG_TYPE_BACKUP_ERROR, requireActivity())
                error(TAG, "isDataAvailable() no data available")
                return false
            }
            return true
        }

    companion object {
        private const val TAG = "LoginFragment"
    }
}