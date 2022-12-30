/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.ui.view_model

import android.content.Context
import com.serhii.apps.notes.database.NotesDatabase
import com.serhii.apps.notes.database.UserNotesDatabase
import com.serhii.apps.notes.ui.data_model.NoteModel

/**
 * Encapsulates logic related to retrieving data from database provider
 */
class NotesRepository(applicationContext: Context, private val callback: NotifyUpdateData) {

    private val notesDatabaseProvider: NotesDatabase<NoteModel>

    init {
        notesDatabaseProvider = UserNotesDatabase.getInstance()
        notesDatabaseProvider.init(applicationContext)
    }

    fun close() {
        notesDatabaseProvider.close()
    }

    fun delete(index: String): Boolean {
        val result = notesDatabaseProvider.deleteRecord(index)
        if (result) {
            callback.updateData()
        }
        return result
    }

    fun add(noteModel: NoteModel): Boolean {
        // Save data in database
        val result = notesDatabaseProvider.addRecord(noteModel)
        if (result != -1 && result != 0) {
            callback.updateData()
            return true
        }

        // Failed to save data
        return false
    }

    fun update(index: String, noteModel: NoteModel): Boolean {
        val result = notesDatabaseProvider.updateRecord(index, noteModel)
        if (result) {
            callback.updateData()
        }
        return result
    }

    fun get(index: String): NoteModel? {
        return notesDatabaseProvider.getRecord(index)
    }

    fun getAll(): List<NoteModel> {
        return notesDatabaseProvider.getRecords()
    }

}

/**
 * Interface for notifying View Model that data has changed recently
 */
interface NotifyUpdateData {
    fun updateData()
}