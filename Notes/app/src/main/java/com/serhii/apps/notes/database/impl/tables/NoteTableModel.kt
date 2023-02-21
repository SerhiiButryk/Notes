/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.database.impl.tables

import android.provider.BaseColumns

object NoteTableModel {
    /**
     * Db table query constants
     */
    const val DATABASE_NAME = "notes.db"
    const val VERSION = 1

    /**
     * Db queries
     */
    const val QUERY_CREATE_USER_NOTES_TABLE = ("CREATE TABLE "
            + UserNotesEntry.TABLE_NAME + " ("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + UserNotesEntry.COLUMN_NAME_NOTE + " TEXT NOT NULL );")

    const val QUERY_DROP_USER_NOTES_TABLE = "DROP TABLE IF EXISTS " + UserNotesEntry.TABLE_NAME

    /**
     * Db table description for user notes
     */
    object UserNotesEntry : BaseColumns {
        const val TABLE_NAME = "notesList"
        const val COLUMN_NAME_NOTE = "note"
        const val _ID = "_id"
    }
}