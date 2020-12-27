package com.example.notes.test.database.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

import static com.example.notes.test.database.impl.model.NoteTableModel.*;

public class NoteDBHelper extends SQLiteOpenHelper {

    public NoteDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(QUERY_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void deleteTable(SQLiteDatabase db) {
        db.execSQL(QUERY_DROP_TABLE);
    }
}
