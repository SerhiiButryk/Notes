/*
 * Copyright 2024. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.repository

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

    fun delete(index: String): Boolean {
        val result = UserNotesDatabase.deleteRecord(index)
        if (result) {
            callback.updateData()
        }
        return result
    }

    fun add(noteModel: NoteModel): Boolean {
        // Save data in database
        val result = UserNotesDatabase.addRecord(noteModel)
        if (result != -1 && result != 0) {
            callback.updateData()
            return true
        }
        // Failed to save data
        return false
    }

    fun update(index: String, noteModel: NoteModel): Boolean {
        val result = UserNotesDatabase.updateRecord(index, noteModel)
        if (result) {
            callback.updateData()
        }
        return result
    }

    fun get(index: String): NoteModel {
        return UserNotesDatabase.getRecord(index)
    }

    fun getAll(): List<NoteModel> {
        return UserNotesDatabase.getRecords()
    }

}

/**
 * Interface for notifying View Model that data has been changed
 */
interface DataChangedListener {
    fun updateData()
}