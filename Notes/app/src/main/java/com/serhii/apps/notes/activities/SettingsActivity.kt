/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.serhii.apps.notes.R
import com.serhii.apps.notes.common.App
import com.serhii.apps.notes.control.backup.BackupManager
import com.serhii.apps.notes.control.preferences.PreferenceManager
import com.serhii.apps.notes.ui.SettingItem
import com.serhii.apps.notes.ui.SettingsUI
import com.serhii.apps.notes.ui.state_holders.SettingsViewModel
import com.serhii.apps.notes.ui.theme.AppMaterialTheme
import com.serhii.core.log.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Activity for app settings
 */
class SettingsActivity : AppBaseActivity() {

    private val viewModel: SettingsViewModel by viewModels()

    init {
        APP_BASE_TAG += SettingsActivity.TAG
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            viewModel.initViewModel(this)
            setupData()
        }

        setContent {
            AppMaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Box(Modifier.safeDrawingPadding()) {
                        SettingsUI(viewModel = viewModel)
                    }
                }
            }
        }

    }

    private fun setupData() {

        val settingItemList = mutableListOf(

            SettingItem(
                imageId = R.drawable.shield_lock,
                onClick = {

                    val timeout = PreferenceManager.getTimeout(applicationContext)

                    val createOptionDialog = { text: Int, time: Long ->
                        SettingsViewModel.DialogOption(text, isSelected = timeout == time) {
                            lifecycleScope.launch(App.UI_DISPATCHER) {
                                delay(1*500) // Small delay so user can see click animation
                                PreferenceManager.setTimeout(applicationContext, time)
                                viewModel.closeDialog()
                            }
                        }
                    }

                    val TIME_3_MIN = 3 * 60 * 1000L
                    val TIME_5_MIN = 5 * 60 * 1000L
                    val TIME_10_MIN = 10 * 60 * 1000L
                    val TIME_NEVER = 0L

                    val options = listOf(
                        createOptionDialog(R.string.s_3_min, TIME_3_MIN),
                        createOptionDialog(R.string.s_5_min, TIME_5_MIN),
                        createOptionDialog(R.string.s_10_min, TIME_10_MIN),
                        createOptionDialog(R.string.never, TIME_NEVER)
                    )

                    viewModel.openOptionListDialog(options)
                },
                titleString = getString(R.string.preference_idle_lock_timeout_title),
                subTitleString = getString(R.string.preference_idle_lock_timeout_subtitle)
            ),

            SettingItem(
                Icons.Default.Backup,
                {
                    BackupManager.openDirectoryChooserForExtractData(this)
                },
                getString(R.string.preference_extract_title),
                getString(R.string.preference_extract_desc)
            ),

            SettingItem(
                Icons.Default.Lock,
                {
                    BackupManager.openDirectoryChooserForBackup(this)
                },
                getString(R.string.preference_backup_title),
                getString(R.string.preference_backup_desc)
            ),

            SettingItem(
                Icons.Default.Restore,
                {
                    BackupManager.openFileChooser(this)
                },
                getString(R.string.preference_restore_note_title),
                getString(R.string.preference_restore_note_decs)
            ),

            SettingItem(
                hasSwitch = true,
                onSwitch = { checked ->
                    Log.enableDetailedLogs(checked)
                    PreferenceManager.setDetailedLogs(applicationContext, checked)
                },
                titleString = getString(R.string.preference_category_title_debug),
                subTitleString = getString(R.string.preference_category_message_detail_logs),
                switchState = PreferenceManager.detailedLogsEnabled(applicationContext)
            ),

            SettingItem(
                imageId = R.drawable.help,
                onClick = {
                    // Open email client app for sending feedback
                    val mailto = Uri.parse(
                        "mailto:${App.DEV_EMAIL}?" +
                                "subject=" +
                                getString(R.string.email_feedback_title) +
                                "&body=" +
                                getString(R.string.email_feedback_body)
                    )

                    val intent = Intent(Intent.ACTION_SENDTO, mailto)

                    try {
                        startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.error(TAG, "openEmailClientApp() error: $e")
                    }
                },
                titleString = getString(R.string.preference_feedback_title),
                subTitleString = getString(R.string.preference_feedback_message)
            ),

            SettingItem(
                Icons.Default.Info,
                {},
                getString(R.string.preference_about_version_title),
                App.VERSION
            ),
            SettingItem(
                imageId = R.drawable.info,
                titleString = getString(R.string.info_author),
                subTitleString = getString(R.string.info_author_sub)
            )
        )

        viewModel.updateState(settingItemList)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (BackupManager.REQUEST_CODE_EXTRACT_NOTES == requestCode && resultCode == RESULT_OK) {
            Log.info(TAG, "onActivityResult() got result for REQUEST_CODE_EXTRACT_NOTES")
            if (data != null) {
                viewModel.onExtract(applicationContext, data)
            } else {
                // Should not happen
                Log.error(TAG, "onActivityResult() data is null")
            }
        } else if (requestCode == BackupManager.REQUEST_CODE_BACKUP_NOTES && resultCode == RESULT_OK) {
            Log.info(TAG, "onActivityResult() got result for REQUEST_CODE_BACKUP_NOTES")
            if (data != null) {
                viewModel.openKeywordSetDialog(applicationContext, data)
            } else {
                // Should not happen
                Log.error(TAG, "onActivityResult() data is null")
            }
        } else if (requestCode == BackupManager.REQUEST_CODE_OPEN_BACKUP_FILE && resultCode == RESULT_OK) {
            Log.info(TAG, "onActivityResult() got result for REQUEST_CODE_OPEN_BACKUP_FILE")
            if (data != null) {
                viewModel.openKeywordRequestDialog(application, data)
            } else {
                // Should not happen
                Log.error(TAG, "onActivityResult() data is null")
            }
        }
    }

    companion object {
        private const val TAG = "SettingsActivity"
    }

}