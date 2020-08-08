package com.example.notes.test.db.impl.model;

import android.provider.BaseColumns;

public class DbNoteTableModel {

    private DbNoteTableModel() {}

    /**
     *   Db table description for user notes
     */

    public static final class NotesEntry implements BaseColumns {

        public static final String TABLE_NAME = "notesList";
        public static final String COLUMN_NAME_NOTE = "note";
        public static final String COLUMN_NAME_TITLE = "noteTitle";
        public static final String COLUMN_TIMESTAMP = "time";

    }

    /**
     *  Db table query constants
     */

    public static final String DATABASE_NAME = "notes.db";
    public static final int VERSION = 1;

    public static final String QUERY_CREATE_TABLE = "CREATE TABLE "
            + DbNoteTableModel.NotesEntry.TABLE_NAME + " ("
            + DbNoteTableModel.NotesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DbNoteTableModel.NotesEntry.COLUMN_NAME_NOTE + " TEXT NOT NULL, "
            + DbNoteTableModel.NotesEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL, "
            + DbNoteTableModel.NotesEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" + ");";

    public static final String QUERY_DROP_TABLE = "DROP TABLE IF EXISTS " + NotesEntry.TABLE_NAME;
}
