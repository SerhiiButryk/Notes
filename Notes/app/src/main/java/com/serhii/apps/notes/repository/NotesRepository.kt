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
class NotesRepository() {

    fun close() {
        UserNotesDatabase.close()
    }

    fun delete(index: String): Boolean {
        return UserNotesDatabase.deleteRecord(index)
    }

    fun add(noteModel: NoteModel): Int {
        // Failed to save data
        return UserNotesDatabase.addRecord(noteModel)
    }

    fun update(index: String, noteModel: NoteModel): Boolean {
        return UserNotesDatabase.updateRecord(index, noteModel)
    }

    fun get(index: String): NoteModel {
        return UserNotesDatabase.getRecord(index)
    }

    fun getAll(): List<NoteModel> {
        return UserNotesDatabase.getRecords()
    }

}