/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.serhii.apps.notes.R
import com.serhii.apps.notes.control.backup.BackupManager
import com.serhii.apps.notes.ui.fragments.SettingsFragment
import com.serhii.core.log.Log.Companion.error
import com.serhii.core.log.Log.Companion.info
import com.serhii.core.utils.GoodUtils.Companion.showToast
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * Activity for app settings
 */
class SettingsActivity : AppBaseActivity() {

    private var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
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
                extractNotes(data)
            } else {
                // Should not happen
                info(TAG, "onActivityResult() 1")
            }
        } else if (requestCode == BackupManager.REQUEST_CODE_BACKUP_NOTES && resultCode == RESULT_OK) {
            info(TAG, "onActivityResult() got result for REQUEST_CODE_BACKUP_NOTES")
            if (data != null) {
                backupNotes(data)
            } else {
                // Should not happen
                info(TAG, "onActivityResult() 2")
            }
        } else if (requestCode == BackupManager.REQUEST_CODE_OPEN_BACKUP_FILE && resultCode == RESULT_OK) {
            info(TAG, "onActivityResult() got result for REQUEST_CODE_OPEN_BACKUP_FILE")
            if (data != null) {
                restoreNotes(data)
            } else {
                // Should not happen
                info(TAG, "onActivityResult() 3")
            }
        }

    }

    private fun extractNotes(data: Intent) {
        var outputStream: OutputStream? = null
        outputStream = try {
            contentResolver.openOutputStream(data.data!!)
        } catch (e: FileNotFoundException) {
            error(TAG, "extractNotes() error: $e")
            e.printStackTrace()
            return
        }
        val result = BackupManager.saveDataAsPlainText(outputStream, this)
        if (result) {
            showToast(this, R.string.result_success)
        } else {
            showToast(this, R.string.result_failed)
        }
    }

    private fun backupNotes(data: Intent) {
        info(TAG, "backupNotes() IN")
        var outputStream: OutputStream? = null
        outputStream = try {
            contentResolver.openOutputStream(data.data!!)
        } catch (e: FileNotFoundException) {
            error(TAG, "onOkClicked() error: $e")
            e.printStackTrace()
            return
        }
        val result = BackupManager.backupData(outputStream, this@SettingsActivity)
        if (result) {
            showToast(this@SettingsActivity, R.string.result_success)
        } else {
            showToast(this@SettingsActivity, R.string.result_failed)
        }
    }

    private fun restoreNotes(data: Intent) {
        info(TAG, "restoreNotes() IN")
        val json = readBackupFile(data)
        val result = BackupManager.restoreData(json, this@SettingsActivity)
        if (result) {
            showToast(this@SettingsActivity, R.string.result_success)
        } else {
            showToast(this@SettingsActivity, R.string.result_failed)
        }
    }

    @SuppressLint("Recycle")
    private fun readBackupFile(data: Intent): String {
        var inputStream: InputStream? = null
        inputStream = try {
            contentResolver.openInputStream(data.data!!)
        } catch (e: FileNotFoundException) {
            error(TAG, "readBackupFile() error: $e")
            e.printStackTrace()
            return ""
        }
        val content = StringBuilder()
        try {
            val buffer = ByteArray(inputStream!!.available())
            while (inputStream.read(buffer) != -1) {
                content.append(String(buffer))
            }
        } catch (e: Exception) {
            error(TAG, "readBackupFile() exception while reading the file: $e")
            e.printStackTrace()
            return ""
        } finally {
            try {
                inputStream!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return content.toString()
    }

    companion object {
        private const val TAG = "SettingsActivity"
        private const val SETTINGS_FRAGMENT_TAG = "main settings"
    }

}