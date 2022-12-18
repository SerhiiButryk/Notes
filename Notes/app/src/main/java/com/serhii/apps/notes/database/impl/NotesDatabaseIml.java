/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.database.impl;

import static com.serhii.apps.notes.database.impl.tables.NoteTableModel.QUERY_DROP_USER_NOTES_TABLE;
import static com.serhii.apps.notes.database.impl.tables.NoteTableModel.UserNotesEntry;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.serhii.apps.notes.database.impl.database.DatabaseImpl;
import com.serhii.core.log.Log;

import java.util.HashMap;
import java.util.Map;

/**
 *  Database impl for user notes
 */
public class NotesDatabaseIml extends DatabaseImpl {

    private static final String TAG = "NoteDatabaseIml";

    private SQLiteDatabase databaseWrite;
    private SQLiteDatabase databaseRead;

    private static final NotesDatabaseIml instance = new NotesDatabaseIml();

    private NotesDatabaseIml() {}

    public static NotesDatabaseIml getInstance() {
        return instance;
    }

    @Override
    public void initDbImpl(Context context) {
        if (!initialized) {
            Log.info(TAG, "initDbImpl() init database");

            NotesDBHelper noteDBHelper = new NotesDBHelper(context);

            databaseWrite = noteDBHelper.getWritableDatabase();
            databaseRead = noteDBHelper.getReadableDatabase();

            initialized = true;
        } else {
            Log.info(TAG, "initDbImpl() no-op");
        }
    }

    @Override
    public void clearDatabaseImpl() {
        super.clearDatabaseImpl();

        databaseWrite.execSQL(QUERY_DROP_USER_NOTES_TABLE);
    }

    @Override
    public int addRecordImpl(final String data) {
        super.addRecordImpl(data);

        Log.info(TAG, "addRecordImpl()");

        ContentValues values = new ContentValues();
        values.put(UserNotesEntry.COLUMN_NAME_NOTE, data);

        long row = databaseWrite.insert(UserNotesEntry.TABLE_NAME, null, values);

        if (row == -1) {
            Log.error(TAG, "addRecordImpl(), failed to insert values to database");
        } else {
            Log.info(TAG, "addRecordImpl(), inserted, row = " + row);
        }

        return (int) row;
    }

    @Override
    public boolean deleteRecordImpl(final String id) {
        super.deleteRecordImpl(id);

        Log.info(TAG, "deleteRecordImpl(), id=" + id);

        String selection = UserNotesEntry._ID + FLAG_SELECT;
        String[] selectionArgs = { String.valueOf(id) };

        int count = databaseWrite.delete(UserNotesEntry.TABLE_NAME, selection, selectionArgs);

        Log.info(TAG, "deleteRecordImpl(), deleted rows=" + count);

        return count > 0;
    }

    @Override
    public boolean updateRecordImpl(final String id, final String newData) {
        super.updateRecordImpl(id, newData);

        Log.info(TAG, "updateRecordImpl() id=" + id);

        ContentValues values = new ContentValues();
        values.put(UserNotesEntry.COLUMN_NAME_NOTE, newData);

        int count = databaseWrite.update(UserNotesEntry.TABLE_NAME, values, UserNotesEntry._ID + FLAG_SELECT,
                new String[]{ String.valueOf(id) });

        if (count == 0) {
            Log.error(TAG, "updateRecordImpl(), no rows updated");
        }

        return count != 0;
    }

    @Override
    public String getRecordImpl(final String id) {
        super.getRecordImpl(id);
        return getRecord(id);
    }

    @SuppressLint({"Range", "Recycle"})
    @Override
    public Map<Integer, String> getRecordsImpl() {
        super.getRecordsImpl();

        Map<Integer, String> data = new HashMap<>();

        Cursor c = databaseRead.rawQuery("SELECT * FROM " + UserNotesEntry.TABLE_NAME, null);

        while (c.moveToNext()) {

            String id = c.getString(c.getColumnIndex(UserNotesEntry._ID));
            String note = c.getString(c.getColumnIndex(UserNotesEntry.COLUMN_NAME_NOTE));

            data.put(Integer.parseInt(id), note);
        }

        return data;
    }

    @Override
    public void closeImpl() {
        super.closeImpl();

        databaseWrite.close();
        databaseRead.close();

        databaseRead = null;
        databaseWrite = null;

        initialized = false;
    }

    @Override
    public int getRecordsCountImpl() {

        long count = DatabaseUtils.queryNumEntries(databaseRead, UserNotesEntry.TABLE_NAME);

        Log.info(TAG, "getRecordsCount(), count = " + count);

        return (int) count;
    }

    @SuppressLint("Range")
    private String getRecord(final String id) {

        Log.info(TAG, "getRecord() id=" + id);

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                UserNotesEntry._ID,
                UserNotesEntry.COLUMN_NAME_NOTE
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = UserNotesEntry._ID + FLAG_SELECT;
        String[] selectionArgs = { id };

        Cursor c = databaseRead.query(UserNotesEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);

        if (c.getCount() == 0) {
            Log.error(TAG, "getRecord(), no records found");
        } else {
            Log.info(TAG, "getRecord(), row count:" + c.getCount());
        }

        String data = null;

        if (c.moveToNext()) {
            data = c.getString(c.getColumnIndex(UserNotesEntry.COLUMN_NAME_NOTE));
        }

        c.close();

        return data;
    }

}
