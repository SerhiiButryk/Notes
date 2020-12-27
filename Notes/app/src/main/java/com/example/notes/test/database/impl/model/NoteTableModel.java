package com.example.notes.test.database.impl.model;

import android.provider.BaseColumns;

public class NoteTableModel {

    private NoteTableModel() {}

    /**
     *   Db table description for user notes
     */

    public static final class NotesEntry implements BaseColumns {

        public static final String TABLE_NAME = "notesList";
        public static final String COLUMN_NAME_NOTE = "note";
        public static final String COLUMN_NAME_TITLE = "noteTitle";
        public static final String COLUMN_NAME_TIMESTAMP = "time";

    }

    /**
     *  Db table query constants
     */

    public static final String DATABASE_NAME = "notes.db";
    public static final int VERSION = 1;

    public static final String QUERY_CREATE_TABLE = "CREATE TABLE "
            + NoteTableModel.NotesEntry.TABLE_NAME + " ("
            + NoteTableModel.NotesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NoteTableModel.NotesEntry.COLUMN_NAME_NOTE + " TEXT NOT NULL, "
            + NoteTableModel.NotesEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL, "
            + NoteTableModel.NotesEntry.COLUMN_NAME_TIMESTAMP + " TEXT NOT NULL " + ");";

    public static final String QUERY_DROP_TABLE = "DROP TABLE IF EXISTS " + NotesEntry.TABLE_NAME;
}
