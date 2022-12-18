/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.control.backup

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.serhii.apps.notes.database.UserNotesDatabase
import com.serhii.apps.notes.ui.data_model.NoteModel
import com.serhii.apps.notes.ui.view_model.NotesViewModel
import com.serhii.core.log.Log.Companion.detail
import com.serhii.core.log.Log.Companion.error
import com.serhii.core.log.Log.Companion.info
import com.squareup.moshi.Moshi
import java.io.IOException
import java.io.OutputStream
import java.lang.ref.WeakReference

object BackupManager {

    private var notesViewModelWeakReference: WeakReference<NotesViewModel>? = null

    fun setNotesViewModelWeakReference(notesViewModel: NotesViewModel?) {
        notesViewModelWeakReference = WeakReference(notesViewModel)
    }

    fun clearNotesViewModelWeakReference() {
        if (notesViewModelWeakReference != null) {
            notesViewModelWeakReference!!.clear()
        }
    }

    fun openDirectoryChooserForExtractData(activity: Activity) {
        info(TAG, "openDirectoryChooser()")
        activity.startActivityForResult(
            createIntent(FILE_NAME_EXTRACTED_DATA),
            REQUEST_CODE_EXTRACT_NOTES
        )
    }

    fun openDirectoryChooserForBackup(activity: Activity) {
        info(TAG, "openDirectoryChooserForBackup()")
        activity.startActivityForResult(
            createIntent(FILE_NAME_BACKUP_DATA),
            REQUEST_CODE_BACKUP_NOTES
        )
    }

    fun openBackUpFile(activity: Activity) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = FILE_MIME_TYPE
        intent.putExtra(Intent.EXTRA_TITLE, FILE_NAME_BACKUP_DATA)
        activity.startActivityForResult(intent, REQUEST_CODE_OPEN_BACKUP_FILE)
    }

    fun saveDataAsPlainText(outputStream: OutputStream?, context: Context?): Boolean {
        info(TAG, "backupDataAsPlainText()")
        try {
            val notesDatabaseProvider = UserNotesDatabase.getInstance()
            notesDatabaseProvider.init(context)

            if (notesDatabaseProvider.recordsCount != 0) {
                val notes = notesDatabaseProvider.records
                val builder = StringBuilder()
                for (note in notes) {
                    builder.append("*********** ")
                        .append(note.title.trim { it <= ' ' })
                        .append(" ***********")
                        .append("\n")
                        .append(note.note.trim { it <= ' ' })
                        .append("\n")
                }
                val data = builder.toString()
                if (!data.isEmpty()) {
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

    fun backupData(outputStream: OutputStream?, context: Context?): Boolean {

        val notesDatabaseProvider = UserNotesDatabase.getInstance()
        notesDatabaseProvider.init(context)

        info(TAG, "backupDataAsEncryptedText() records: " + notesDatabaseProvider.recordsCount)

        if (notesDatabaseProvider.recordsCount != 0) {
            val notes = notesDatabaseProvider.records
            val serializedNotes: MutableList<NoteAdapter> = ArrayList()

            for (i in notes.indices) {
                serializedNotes.add(NoteAdapter(i, notes[i].title, notes[i].note))
            }

            val backupAdapter = BackupAdapter(serializedNotes)
            val moshi = Moshi.Builder().build()
            val jsonAdapter = moshi.adapter(
                BackupAdapter::class.java
            )

            val json = jsonAdapter.toJson(backupAdapter)
            try {
                outputStream?.write(json.toByteArray())
                outputStream?.flush()
                outputStream?.close()
            } catch (e: IOException) {
                info(TAG, "backupDataAsEncryptedText() error: $e")
                e.printStackTrace()
                return false
            }
            return true
        } else {
            info(TAG, "backupDataAsEncryptedText() database is empty")
        }

        return false
    }

    fun restoreData(json: String, context: Context?): Boolean {
        detail(TAG, "restoreData() json: $json")

        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter(BackupAdapter::class.java)

        var backupAdapter: BackupAdapter? = null
        backupAdapter = try {
            jsonAdapter.fromJson(json)
        } catch (e: IOException) {
            e.printStackTrace()
            info(TAG, "restoreData() error parsing json content: $e")
            return false
        }

        val notes = backupAdapter!!.notes
        val notesDatabaseProvider = UserNotesDatabase.getInstance()
        notesDatabaseProvider.init(context)

        for (note in notes) {
            notesDatabaseProvider.addRecord(NoteModel(note.note, note.title))
        }

        if (notesViewModelWeakReference != null) {
            val notesViewModel = notesViewModelWeakReference!!.get()
            if (notesViewModel != null) {
                notesViewModel.updateData()
            } else {
                error(TAG, "restoreData() nvm is null")
                return false
            }
        } else {
            error(TAG, "restoreData() wr is null")
            return false
        }
        return true
    }

    private fun createIntent(fileName: String): Intent {
        // Show directory chooser
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = FILE_MIME_TYPE
        intent.putExtra(Intent.EXTRA_TITLE, fileName)
        return intent
    }

    /**
     * Backup Adapter
     */
    class BackupAdapter(var notes: List<NoteAdapter>)
    class NoteAdapter(private val id: Int, val title: String, val note: String)

    private const val TAG = "BackupManager"
    const val REQUEST_CODE_EXTRACT_NOTES = 1
    const val REQUEST_CODE_BACKUP_NOTES = 2
    const val REQUEST_CODE_OPEN_BACKUP_FILE = 3

    private const val FILE_MIME_TYPE = "text/plain"
    private const val FILE_NAME_EXTRACTED_DATA = "NotesExtracted.txt"
    private const val FILE_NAME_BACKUP_DATA = "NotesBackup.txt"
    private const val IV_KEY_MARKER = "RTSASPE00"

}