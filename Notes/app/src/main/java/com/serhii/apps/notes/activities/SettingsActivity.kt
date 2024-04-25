/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.serhii.apps.notes.R
import com.serhii.apps.notes.common.App
import com.serhii.apps.notes.control.backup.BackupManager
import com.serhii.apps.notes.database.UserNotesDatabase
import com.serhii.apps.notes.ui.EnterPasswordDialogUI
import com.serhii.apps.notes.ui.dialogs.DialogHelper
import com.serhii.apps.notes.ui.fragments.SettingsFragment
import com.serhii.core.log.Log
import com.serhii.core.log.Log.Companion.error
import com.serhii.core.log.Log.Companion.info
import com.serhii.core.utils.GoodUtils
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.InputStream
import java.io.OutputStream

/**
 * Activity for app settings
 */
class SettingsActivity : AppBaseActivity() {

    private var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setLoggingTagForActivity(TAG)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        toolbar = findViewById(R.id.toolbar)
        toolbar?.setNavigationOnClickListener { onBackPressed() }

        if (savedInstanceState == null) {
            addFragment()
        }

        setActionBar()
    }

    private fun addFragment() {
        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true) // Needed for optimization
            .replace(R.id.container, SettingsFragment(), SETTINGS_FRAGMENT_TAG)
            .commit()
    }

    private fun setActionBar() {
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = getString(R.string.preference_title)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("Recycle")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (BackupManager.REQUEST_CODE_EXTRACT_NOTES == requestCode && resultCode == RESULT_OK) {
            info(TAG, "onActivityResult() got result for REQUEST_CODE_EXTRACT_NOTES")
            if (data != null) {

                val outputStream: OutputStream? = try {
                    contentResolver.openOutputStream(data.data!!)
                } catch (e: FileNotFoundException) {
                    error(TAG, "onActivityResult() failed to get output stream, error: $e")
                    e.printStackTrace()
                    return
                }

                // Start backup
                lifecycleScope.launch(App.BACKGROUND_DISPATCHER) {
                    if (UserNotesDatabase.recordsCount != 0) {
                        val notes = UserNotesDatabase.getRecords()
                        BackupManager.extractNotes(outputStream, notes) { result ->
                            withContext(App.UI_DISPATCHER) {
                                showStatusMessage(result)
                            }
                        }
                    } else {
                        info(TAG, "onActivityResult() no data")
                    }
                }
            } else {
                // Should not happen
                error(TAG, "onActivityResult() data is null")
            }
        } else if (requestCode == BackupManager.REQUEST_CODE_BACKUP_NOTES && resultCode == RESULT_OK) {
            info(TAG, "onActivityResult() got result for REQUEST_CODE_BACKUP_NOTES")
            if (data != null) {

                val title = getString(R.string.set_password_dialog_title)
                val hint = getString(R.string.set_password_dialog_hint_backup)

                // Ask for keyword
                DialogHelper.showEnterPasswordField(this, object : EnterPasswordDialogUI.DialogListener {

                    override fun onOk(enteredText: String?, context: Context?) {
                        if (enteredText != null) {

                            val outputStream: OutputStream? = try {
                                val uri = data.data
                                if (uri != null) {
                                    context?.contentResolver?.openOutputStream(uri)
                                } else null
                            } catch (e: FileNotFoundException) {
                                Log.error(TAG, "onActivityResult() error: $e")
                                e.printStackTrace()
                                return
                            }

                            if (outputStream == null) {
                                Log.error(TAG, "onActivityResult() error: outputStream == null")
                                return
                            }

                            // Start backup
                            lifecycleScope.launch(App.BACKGROUND_DISPATCHER) {
                                BackupManager.backupNotes(enteredText, outputStream) { result ->
                                    withContext(App.UI_DISPATCHER) {
                                        showStatusMessage(result)
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancel(context: Context?) {
                    }
                }, title, hint)

            } else {
                // Should not happen
                error(TAG, "onActivityResult() data is null")
            }
        } else if (requestCode == BackupManager.REQUEST_CODE_OPEN_BACKUP_FILE && resultCode == RESULT_OK) {
            info(TAG, "onActivityResult() got result for REQUEST_CODE_OPEN_BACKUP_FILE")
            if (data != null) {

                val title = getString(R.string.set_password_dialog_title)
                val hint = getString(R.string.set_password_dialog_hint_restore)

                // Ask for keyword
                DialogHelper.showEnterPasswordField(this, object : EnterPasswordDialogUI.DialogListener {

                    override fun onOk(enteredText: String?, context: Context?) {
                        if (enteredText != null) {

                            val inputStream: InputStream? = try {
                                context?.contentResolver?.openInputStream(data.data!!)
                            } catch (e: FileNotFoundException) {
                                error(TAG, "onActivityResult() error: $e")
                                e.printStackTrace()
                                return
                            }

                            if (inputStream == null) {
                                Log.error(TAG, "onActivityResult() error: outputStream == null")
                                return
                            }

                            // Start restore
                            lifecycleScope.launch(App.BACKGROUND_DISPATCHER) {
                                BackupManager.restoreNotes(enteredText, inputStream) { result ->
                                    withContext(App.UI_DISPATCHER) {
                                        showStatusMessage(result)
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancel(context: Context?) {
                    }

                }, title, hint)


            } else {
                // Should not happen
                error(TAG, "onActivityResult() data is null")
            }
        }
    }

    private fun showStatusMessage(result: Boolean) {
        if (result) {
            GoodUtils.showToast(baseContext, R.string.result_success)
        } else {
            GoodUtils.showToast(baseContext, R.string.result_failed)
        }
    }

    companion object {
        private const val TAG = "SettingsActivity"
        private const val SETTINGS_FRAGMENT_TAG = "SettingsActivityTAG"
    }

}