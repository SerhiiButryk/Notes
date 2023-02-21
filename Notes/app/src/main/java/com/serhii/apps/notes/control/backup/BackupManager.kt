/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.control.backup

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.serhii.apps.notes.database.UserNotesDatabase
import com.serhii.apps.notes.ui.data_model.NoteModel
import com.serhii.apps.notes.ui.view_model.NotesViewModel
import com.serhii.core.log.Log.Companion.detail
import com.serhii.core.log.Log.Companion.error
import com.serhii.core.log.Log.Companion.info
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.IOException
import java.io.OutputStream
import java.lang.ref.WeakReference

object BackupManager {

    // View Model is observing changes to this field to get new data
    private val updateDataFlag: MutableLiveData<Boolean> = MutableLiveData(false)

    fun getUpdateDataFlagData() : LiveData<Boolean> {
        return updateDataFlag
    }

    fun dataUpdated() {
        updateDataFlag.value = false
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

    fun saveDataAsPlainText(outputStream: OutputStream?, context: Context): Boolean {
        info(TAG, "backupDataAsPlainText()")

        try {

            if (UserNotesDatabase.recordsCount != 0) {
                val notes = UserNotesDatabase.getRecords(context)
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
                    info(TAG, "backupDataAsPlainText() backup is finished")
                    return true
                }
            } else {
                info(TAG, "backupDataAsPlainText() database is empty")
            }
        } catch (e: Exception) {
            error(TAG, "backupDataAsPlainText() exception: $e")
            e.printStackTrace()
        }

        return false
    }

    fun backupData(outputStream: OutputStream?, context: Context): Boolean {
        info(TAG, "backupDataAsEncryptedText() found records: " + UserNotesDatabase.recordsCount)

        if (UserNotesDatabase.recordsCount != 0) {
            val notes = UserNotesDatabase.getRecords(context)
            val serializedNotes = mutableListOf<NoteAdapter>()

            for ((index, note) in notes.withIndex()) {
                serializedNotes.add(NoteAdapter("$index", NoteModel.getJson(note)))
            }

            val backupAdapter = BackupAdapter(serializedNotes)

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val jsonAdapter = moshi.adapter(BackupAdapter::class.java)

            val json = jsonAdapter.toJson(backupAdapter)

            try {
                outputStream?.write(json.toByteArray())
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

    fun restoreData(json: String, context: Context): Boolean {
        detail(TAG, "restoreData() IN")

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val jsonAdapter: JsonAdapter<BackupAdapter> = moshi.adapter(BackupAdapter::class.java)

        val backupAdapter: BackupAdapter? = try {
            jsonAdapter.fromJson(json)
        } catch (e: IOException) {
            error(TAG, "restoreData() error parsing json content: $e")
            e.printStackTrace()
            return false
        }

        val notes = backupAdapter!!.notes

        for (note in notes) {
            val noteModel: NoteModel = NoteModel.fromJson(note.note)
            UserNotesDatabase.addRecord(noteModel, context)
        }

        updateDataFlag.value = true

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

    class BackupAdapter(val notes: List<NoteAdapter>)

    class NoteAdapter(val id: String, val note: String)

    private const val TAG = "BackupManager"
    const val REQUEST_CODE_EXTRACT_NOTES = 1
    const val REQUEST_CODE_BACKUP_NOTES = 2
    const val REQUEST_CODE_OPEN_BACKUP_FILE = 3

    private const val FILE_MIME_TYPE = "text/plain"
    private const val FILE_NAME_EXTRACTED_DATA = "NotesExtracted.txt"
    private const val FILE_NAME_BACKUP_DATA = "NotesBackup.txt"
}