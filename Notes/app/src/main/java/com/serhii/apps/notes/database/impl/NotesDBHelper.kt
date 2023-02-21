/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.database.impl

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.serhii.apps.notes.database.impl.tables.NoteTableModel.DATABASE_NAME
import com.serhii.apps.notes.database.impl.tables.NoteTableModel.QUERY_CREATE_USER_NOTES_TABLE
import com.serhii.apps.notes.database.impl.tables.NoteTableModel.QUERY_DROP_USER_NOTES_TABLE
import com.serhii.apps.notes.database.impl.tables.NoteTableModel.VERSION

class NotesDBHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(QUERY_CREATE_USER_NOTES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    fun deleteTable(db: SQLiteDatabase) {
        db.execSQL(QUERY_DROP_USER_NOTES_TABLE)
    }
}