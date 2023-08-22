/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.serhii.apps.notes.R
import com.serhii.apps.notes.control.background_work.BackgroundWorkHandler
import com.serhii.apps.notes.control.background_work.WorkId
import com.serhii.apps.notes.control.background_work.WorkItem
import com.serhii.apps.notes.control.backup.BackupManager
import com.serhii.apps.notes.ui.DialogWithEnterFiled
import com.serhii.apps.notes.ui.dialogs.DialogHelper
import com.serhii.apps.notes.ui.fragments.SettingsFragment
import com.serhii.core.log.Log.Companion.error
import com.serhii.core.log.Log.Companion.info

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

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (BackupManager.REQUEST_CODE_EXTRACT_NOTES == requestCode && resultCode == RESULT_OK) {
            info(TAG, "onActivityResult() got result for REQUEST_CODE_EXTRACT_NOTES")
            if (data != null) {

                val workItem = WorkItem(WorkId.EXTRACT_DATA_WORK_ID, 0, { ctx, workItem ->
                    BackupManager.extractNotes(workItem.extraData as Intent, ctx)
                }, null, null)

                workItem.extraData = data

                BackgroundWorkHandler.putWork(workItem, this)
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
                DialogHelper.showEnterPasswordField(this, object : DialogWithEnterFiled.DialogListener {

                    override fun onOkClicked(enteredText: String?, context: Context?) {
                        if (enteredText != null) {

                            val workItem = WorkItem(WorkId.BACKUP_DATA_WORK_ID, 0, { ctx, workItem ->
                                // Backup data
                                BackupManager.backupNotes(workItem.extraData as Intent, enteredText, ctx)
                            }, null, null)

                            workItem.extraData = data

                            BackgroundWorkHandler.putWork(workItem, context!!)
                        }
                    }

                    override fun onCancelClicked(context: Context?) {
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

                // Ask for keyqord
                DialogHelper.showEnterPasswordField(this, object : DialogWithEnterFiled.DialogListener {

                    override fun onOkClicked(enteredText: String?, context: Context?) {
                        if (enteredText != null) {

                            val workItem = WorkItem(WorkId.RESTORE_DATA_WORK_ID, 0, { ctx, workItem ->
                                BackupManager.restoreNotes(workItem.extraData as Intent, enteredText, ctx)
                            }, null, null)

                            workItem.extraData = data

                            BackgroundWorkHandler.putWork(workItem, context!!)
                        }

                    }

                    override fun onCancelClicked(context: Context?) {
                    }
                }, title, hint)


            } else {
                // Should not happen
                error(TAG, "onActivityResult() data is null")
            }
        }

    }

    companion object {
        private const val TAG = "SettingsActivity"
        private const val SETTINGS_FRAGMENT_TAG = "SettingsActivityTAG"
    }

}