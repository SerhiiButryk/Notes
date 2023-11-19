/*
 * Copyright 2023. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.ui.repository

import android.content.Context
import com.serhii.apps.notes.database.UserNotesDatabase
import com.serhii.apps.notes.ui.data_model.NoteModel

/**
 * The class is an entry point to a database in the app
 * Encapsulates logic related to retrieving data from a database provider
 */
class NotesRepository(private val callback: DataChangedListener) {

    fun close() {
        UserNotesDatabase.close()
    }

    fun delete(index: String, context: Context): Boolean {
        val result = UserNotesDatabase.deleteRecord(index)
        if (result) {
            callback.updateData(context)
        }
        return result
    }

    fun add(noteModel: NoteModel, context: Context): Boolean {
        // Save data in database
        val result = UserNotesDatabase.addRecord(noteModel, context)
        if (result != -1 && result != 0) {
            callback.updateData(context)
            return true
        }
        // Failed to save data
        return false
    }

    fun update(index: String, noteModel: NoteModel, context: Context): Boolean {
        val result = UserNotesDatabase.updateRecord(index, noteModel, context)
        if (result) {
            callback.updateData(context)
        }
        return result
    }

    fun get(index: String, context: Context): NoteModel {
        return UserNotesDatabase.getRecord(index, context)
    }

    fun getAll(context: Context): List<NoteModel> {
        return UserNotesDatabase.getRecords(context)
    }

}

/**
 * Interface for notifying View Model that data has been changed
 */
interface DataChangedListener {
    fun updateData(context: Context)
}