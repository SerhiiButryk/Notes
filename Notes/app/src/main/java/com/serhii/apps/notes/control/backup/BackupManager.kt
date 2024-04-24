/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.control.backup

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.serhii.apps.notes.R
import com.serhii.apps.notes.database.UserNotesDatabase
import com.serhii.apps.notes.ui.data_model.NoteModel
import com.serhii.core.log.Log.Companion.detail
import com.serhii.core.log.Log.Companion.error
import com.serhii.core.log.Log.Companion.info
import com.serhii.core.security.Crypto
import com.serhii.core.utils.GoodUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object BackupManager {

    // View Model is observing changes to this field to know when to get new data
    private val noteShouldBeUpdated: MutableLiveData<Boolean> = MutableLiveData(false)

    suspend fun extractNotes(context: Context, outputStream: OutputStream?, notes: List<NoteModel>) {
        info(TAG, "extractNotes()")
        val result = saveDataAsPlainText(outputStream, notes)
        // Show toast
        withContext(Dispatchers.Main) {
            if (result) {
                GoodUtils.showToast(context, R.string.result_success)
            } else {
                GoodUtils.showToast(context, R.string.result_failed)
            }
        }
    }

    fun backupNotes(data: Intent, key: String, context: Context) {
        info(TAG, "backupNotes() IN")
        val outputStream: OutputStream? = try {
            val uri = data.data
            if (uri != null) {
                context.contentResolver.openOutputStream(uri)
            } else null
        } catch (e: FileNotFoundException) {
            error(TAG, "backupNotes() error: $e")
            e.printStackTrace()
            return
        }

        val result = backupData(outputStream, key, context)
        if (result) {
            GoodUtils.showToast(context, R.string.result_success)
        } else {
            GoodUtils.showToast(context, R.string.result_failed)
        }
    }

    fun restoreNotes(data: Intent, key: String, context: Context) {
        info(TAG, "restoreNotes() IN")
        val json = readBackupFile(data, context)
        val result = restoreData(json, key, context)
        if (result) {
            GoodUtils.showToast(context, R.string.result_success)
        } else {
            GoodUtils.showToast(context, R.string.result_failed)
        }
    }

    @SuppressLint("Recycle")
    fun readBackupFile(data: Intent, context: Context): String {
        val inputStream: InputStream? = try {
            context.contentResolver.openInputStream(data.data!!)
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
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return content.toString()
    }

    fun getUpdateDataFlagData() : LiveData<Boolean> {
        return noteShouldBeUpdated
    }

    fun onDataUpdated() {
        noteShouldBeUpdated.value = false
    }

    fun openDirectoryChooserForExtractData(activity: Activity) {
        info(TAG, "openDirectoryChooser()")
        activity.startActivityForResult(createIntent(FILE_NAME_EXTRACTED_DATA), REQUEST_CODE_EXTRACT_NOTES)
    }

    fun openDirectoryChooserForBackup(activity: Activity) {
        info(TAG, "openDirectoryChooserForBackup()")
        activity.startActivityForResult(createIntent(FILE_NAME_BACKUP_DATA), REQUEST_CODE_BACKUP_NOTES)
    }

    fun openBackUpFile(activity: Activity) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        with(intent) {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = FILE_MIME_TYPE
            putExtra(Intent.EXTRA_TITLE, FILE_NAME_BACKUP_DATA)
        }
        activity.startActivityForResult(intent, REQUEST_CODE_OPEN_BACKUP_FILE)
    }

    private fun saveDataAsPlainText(outputStream: OutputStream?, notes: List<NoteModel>): Boolean {
        info(TAG, "saveDataAsPlainText()")
        try {
            if (notes.isNotEmpty()) {
                val builder = StringBuilder()
                for (note in notes) {
                    builder.append("\n")
                        .append(note.title.trim())
                        .append("\n\n")
                        .append(note.note.trim())
                        .append("\n")
                }
                val data = builder.toString()
                if (data.isNotEmpty()) {
                    outputStream?.write(data.toByteArray())
                    outputStream?.flush()
                    outputStream?.close()
                    info(TAG, "saveDataAsPlainText() done")
                    return true
                } else {
                    info(TAG, "saveDataAsPlainText() empty data")
                }
            } else {
                info(TAG, "saveDataAsPlainText() empty note list")
            }
        } catch (e: Exception) {
            error(TAG, "saveDataAsPlainText() error: $e")
            e.printStackTrace()
        }
        return false
    }

    private fun backupData(outputStream: OutputStream?, key: String, context: Context): Boolean {
        info(TAG, "backupDataAsEncryptedText() found records: " + UserNotesDatabase.recordsCount)

        if (UserNotesDatabase.recordsCount != 0) {
            val notes = UserNotesDatabase.getRecords()
            val json = NoteModel.convertNoteListToJson(notes)

            val crypto = Crypto(Crypto.CRYPTO_PROVIDER_OPENSSL)
            val result = crypto.encrypt(json, key)

            try {
                outputStream?.write(result.message.toByteArray())
                outputStream?.flush()
                outputStream?.close()
            } catch (e: IOException) {
                error(TAG, "backupDataAsEncryptedText() error: $e")
                e.printStackTrace()
                return false
            }

            info(TAG, "backupDataAsEncryptedText() success OUT")
            return true
        } else {
            info(TAG, "backupDataAsEncryptedText() database is empty")
        }

        info(TAG, "backupDataAsEncryptedText() failure OUT")
        return false
    }

    private fun restoreData(json: String, key: String, context: Context): Boolean {
        detail(TAG, "restoreData() IN")

        val crypto = Crypto(Crypto.CRYPTO_PROVIDER_OPENSSL)
        val messageJson = crypto.decrypt(json, key)

        val notes = NoteModel.convertJsonToNoteList(messageJson.message)

        for (note in notes) {
            UserNotesDatabase.addRecord(note)
        }

        noteShouldBeUpdated.value = true

        return true
    }

    private fun createIntent(fileName: String): Intent {
        // Show directory chooser
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        with(intent) {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = FILE_MIME_TYPE
            putExtra(Intent.EXTRA_TITLE, fileName)
        }
        return intent
    }

    private const val TAG = "BackupManager"
    const val REQUEST_CODE_EXTRACT_NOTES = 1
    const val REQUEST_CODE_BACKUP_NOTES = 2
    const val REQUEST_CODE_OPEN_BACKUP_FILE = 3

    private const val FILE_MIME_TYPE = "text/plain"
    private const val FILE_NAME_EXTRACTED_DATA = "NotesExtracted.txt"
    private const val FILE_NAME_BACKUP_DATA = "NotesBackup.txt"
}