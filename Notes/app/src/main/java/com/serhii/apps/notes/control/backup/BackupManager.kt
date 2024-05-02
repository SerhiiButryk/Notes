/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.control.backup

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.serhii.apps.notes.database.UserNotesDatabase
import com.serhii.apps.notes.ui.data_model.NoteModel
import com.serhii.core.log.Log
import com.serhii.core.security.Crypto
import com.serhii.core.security.Hash
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object BackupManager {

    // View Model is observing changes to this field to know when to update UI
    private val noteShouldBeUpdated: MutableLiveData<Boolean> = MutableLiveData(false)

    suspend fun extractNotes(outputStream: OutputStream?, notes: List<NoteModel>, callback: suspend (Boolean) -> Unit) {
        Log.info(TAG, "extractNotes()")
        val result = saveDataAsPlainText(outputStream, notes)
        callback(result)
    }

    suspend fun backupNotes(key: String, outputStream: OutputStream, callback: suspend (Boolean) -> Unit) {
        Log.info(TAG, "backupNotes() starting...")
        val result = backupData(outputStream, key)
        callback(result)
    }

    suspend fun restoreNotes(key: String, inputStream: InputStream, callback: suspend (Boolean) -> Unit) {
        Log.info(TAG, "restoreNotes() starting...")
        val json = readBackupFile(inputStream)
        val result = restoreData(json, key)
        callback(result)
    }

    private fun readBackupFile(inputStream: InputStream): String {
        val content = StringBuilder()
        try {
            val buffer = ByteArray(inputStream.available())
            while (inputStream.read(buffer) != -1) {
                content.append(String(buffer))
            }
        } catch (e: Exception) {
            Log.error(TAG, "readBackupFile() exception while reading the file: $e")
            e.printStackTrace()
            return ""
        } finally {
            try {
                inputStream.close()
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
        noteShouldBeUpdated.postValue(false)
    }

    fun openDirectoryChooserForExtractData(activity: Activity) {
        Log.info(TAG, "openDirectoryChooser()")
        activity.startActivityForResult(createIntent(FILE_NAME_EXTRACTED_DATA), REQUEST_CODE_EXTRACT_NOTES)
    }

    fun openDirectoryChooserForBackup(activity: Activity) {
        Log.info(TAG, "openDirectoryChooserForBackup()")
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
        Log.info(TAG, "saveDataAsPlainText()")
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
                    Log.info(TAG, "saveDataAsPlainText() done")
                    return true
                } else {
                    Log.info(TAG, "saveDataAsPlainText() empty data")
                }
            } else {
                Log.info(TAG, "saveDataAsPlainText() empty note list")
            }
        } catch (e: Exception) {
            Log.error(TAG, "saveDataAsPlainText() error: $e")
            e.printStackTrace()
        }
        return false
    }

    private fun backupData(outputStream: OutputStream?, key: String): Boolean {
        Log.info(TAG, "backupDataAsEncryptedText() found records: " + UserNotesDatabase.recordsCount)

        if (UserNotesDatabase.recordsCount != 0) {
            val notes = UserNotesDatabase.getRecords()
            val json = NoteModel.convertNoteListToJson(notes)

            val crypto = Crypto(Crypto.CRYPTO_PROVIDER_OPENSSL)
            val result = crypto.encrypt(json, Hash.hashMD5(key))

            if (!result.errorOk) {
                Log.error(TAG, "backupDataAsEncryptedText() failed to encrypt")
                return false
            }

            try {
                outputStream?.write(result.message.toByteArray())
                outputStream?.flush()
                outputStream?.close()
            } catch (e: IOException) {
                Log.error(TAG, "backupDataAsEncryptedText() error: $e")
                e.printStackTrace()
                return false
            }

            Log.info(TAG, "backupDataAsEncryptedText() success OUT")
            return true
        } else {
            Log.info(TAG, "backupDataAsEncryptedText() database is empty")
        }

        Log.info(TAG, "backupDataAsEncryptedText() failure OUT")
        return false
    }

    private fun restoreData(json: String, key: String): Boolean {
        Log.info(TAG, "restoreData() IN")

        val crypto = Crypto(Crypto.CRYPTO_PROVIDER_OPENSSL)
        val result = crypto.decrypt(json, Hash.hashMD5(key))

        if (!result.errorOk) {
            Log.error(TAG, "restoreData() failed to decrypt")
            return false
        }

        val notes = NoteModel.convertJsonToNoteList(result.message)

        for (note in notes) {
            UserNotesDatabase.addRecord(note)
        }

        noteShouldBeUpdated.postValue(true)

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