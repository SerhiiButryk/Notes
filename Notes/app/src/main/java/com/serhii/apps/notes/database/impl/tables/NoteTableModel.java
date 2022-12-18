/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.database.impl.tables;

import android.provider.BaseColumns;

public class NoteTableModel {

    private NoteTableModel() {}

    /**
     *   Db table description for user notes
     */
    public static final class UserNotesEntry implements BaseColumns {
        public static final String TABLE_NAME = "notesList";
        public static final String COLUMN_NAME_NOTE = "note";
    }

    /**
     *  Db table query constants
     */
    public static final String DATABASE_NAME = "notes.db";
    public static final int VERSION = 1;

    /**
     * Db queries
     */
    public static final String QUERY_CREATE_USER_NOTES_TABLE = "CREATE TABLE "
            + UserNotesEntry.TABLE_NAME + " ("
            + UserNotesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + UserNotesEntry.COLUMN_NAME_NOTE + " TEXT NOT NULL );";

    public static final String QUERY_DROP_USER_NOTES_TABLE = "DROP TABLE IF EXISTS " + UserNotesEntry.TABLE_NAME;
}
