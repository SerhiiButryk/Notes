/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.serhii.apps.notes.R
import com.serhii.apps.notes.ui.SettingItem
import com.serhii.apps.notes.ui.SettingsUI
import com.serhii.apps.notes.ui.state_holders.SettingsViewModel
import com.serhii.apps.notes.ui.theme.AppMaterialTheme

/**
 * Activity for app settings
 */
class SettingsActivity : AppBaseActivity() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setLoggingTagForActivity(TAG)
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
            SettingItem(Icons.Default.Key, { }, getString(R.string.preference_change_password_title), getString(R.string.preference_change_password_desc)),
            SettingItem(Icons.Default.Lock, { }, getString(R.string.preference_idle_lock_timeout_title), getString(R.string.preference_idle_lock_timeout_title)),
            SettingItem(Icons.Default.Backup, { }, getString(R.string.preference_extract_title), getString(R.string.preference_extract_desc)),
            SettingItem(Icons.Default.Backup, { }, getString(R.string.preference_backup_title), getString(R.string.preference_backup_desc)),
            SettingItem(Icons.Default.Restore, { }, getString(R.string.preference_restore_note_title), getString(R.string.preference_restore_note_decs)),
            SettingItem(Icons.Default.Analytics, { }, getString(R.string.preference_category_message_detail_logs), getString(R.string.preference_category_message_detail_logs_sub)),
            SettingItem(Icons.Default.Feedback, { }, getString(R.string.preference_feedback_title), getString(R.string.preference_feedback_message)),
            SettingItem(Icons.Default.Info, { }, getString(R.string.preference_about_version_title), getString(R.string.preference_about_version_title)),
            SettingItem(Icons.Default.Info, { }, getString(R.string.info_author), getString(R.string.info_author_sub))
        )

        viewModel.updateState(settingItemList)
    }

    @SuppressLint("Recycle")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // TODO: Update
//        if (BackupManager.REQUEST_CODE_EXTRACT_NOTES == requestCode && resultCode == RESULT_OK) {
//            info(TAG, "onActivityResult() got result for REQUEST_CODE_EXTRACT_NOTES")
//            if (data != null) {
//
//                val outputStream: OutputStream? = try {
//                    contentResolver.openOutputStream(data.data!!)
//                } catch (e: FileNotFoundException) {
//                    error(TAG, "onActivityResult() failed to get output stream, error: $e")
//                    e.printStackTrace()
//                    return
//                }
//
//                // Start backup
//                lifecycleScope.launch(App.BACKGROUND_DISPATCHER) {
//                    if (UserNotesDatabase.recordsCount != 0) {
//                        val notes = UserNotesDatabase.getRecords()
//                        BackupManager.extractNotes(outputStream, notes) { result ->
//                            showStatusMessage(result)
//                        }
//                    } else {
//                        info(TAG, "onActivityResult() no data")
//                    }
//                }
//            } else {
//                // Should not happen
//                error(TAG, "onActivityResult() data is null")
//            }
//        } else if (requestCode == BackupManager.REQUEST_CODE_BACKUP_NOTES && resultCode == RESULT_OK) {
//            info(TAG, "onActivityResult() got result for REQUEST_CODE_BACKUP_NOTES")
//            if (data != null) {
//
//                val title = getString(R.string.set_password_dialog_title)
//                val hint = getString(R.string.set_password_dialog_hint_backup)
//
//                // Ask for keyword
//                DialogHelper.showEnterPasswordDialog(this, object : EnterPasswordDialogUI.DialogListener {
//
//                    override fun onOk(enteredText: String?, context: Context?) {
//                        if (enteredText != null) {
//
//                            val outputStream: OutputStream? = try {
//                                val uri = data.data
//                                if (uri != null) {
//                                    context?.contentResolver?.openOutputStream(uri)
//                                } else null
//                            } catch (e: FileNotFoundException) {
//                                Log.error(TAG, "onActivityResult() error: $e")
//                                e.printStackTrace()
//                                return
//                            }
//
//                            if (outputStream == null) {
//                                Log.error(TAG, "onActivityResult() error: outputStream == null")
//                                return
//                            }
//
//                            // Start backup
//                            lifecycleScope.launch(App.BACKGROUND_DISPATCHER) {
//                                BackupManager.backupNotes(enteredText, outputStream) { result ->
//                                    showStatusMessage(result)
//                                }
//                            }
//                        }
//                    }
//
//                    override fun onCancel(context: Context?) {
//                    }
//                }, title, hint)
//
//            } else {
//                // Should not happen
//                error(TAG, "onActivityResult() data is null")
//            }
//        } else if (requestCode == BackupManager.REQUEST_CODE_OPEN_BACKUP_FILE && resultCode == RESULT_OK) {
//            info(TAG, "onActivityResult() got result for REQUEST_CODE_OPEN_BACKUP_FILE")
//            if (data != null) {
//
//                val title = getString(R.string.set_password_dialog_title)
//                val hint = getString(R.string.set_password_dialog_hint_restore)
//
//                // Ask for keyword
//                DialogHelper.showEnterPasswordDialog(this, object : EnterPasswordDialogUI.DialogListener {
//
//                    override fun onOk(enteredText: String?, context: Context?) {
//                        if (enteredText != null) {
//
//                            val inputStream: InputStream? = try {
//                                context?.contentResolver?.openInputStream(data.data!!)
//                            } catch (e: FileNotFoundException) {
//                                error(TAG, "onActivityResult() error: $e")
//                                e.printStackTrace()
//                                return
//                            }
//
//                            if (inputStream == null) {
//                                Log.error(TAG, "onActivityResult() error: outputStream == null")
//                                return
//                            }
//
//                            // Start restore
//                            lifecycleScope.launch(App.BACKGROUND_DISPATCHER) {
//                                BackupManager.restoreNotes(enteredText, inputStream) { result ->
//                                    withContext(App.UI_DISPATCHER) {
//                                        showStatusMessage(result)
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    override fun onCancel(context: Context?) {
//                    }
//
//                }, title, hint)
//
//
//            } else {
//                // Should not happen
//                error(TAG, "onActivityResult() data is null")
//            }
//        }
    }

    companion object {
        private const val TAG = "SettingsActivity"
    }

}