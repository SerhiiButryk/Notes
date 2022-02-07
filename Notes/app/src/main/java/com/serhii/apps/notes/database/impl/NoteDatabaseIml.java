package com.serhii.apps.notes.database.impl;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.serhii.core.log.Log;
import com.serhii.apps.notes.database.impl.base.DatabaseImpl;
import com.serhii.apps.notes.database.impl.model.NoteTableModel;
import com.serhii.apps.notes.ui.data_model.NoteModel;

import java.util.ArrayList;
import java.util.List;

import static com.serhii.apps.notes.database.impl.model.NoteTableModel.NotesEntry;
import static com.serhii.apps.notes.database.impl.model.NoteTableModel.QUERY_DROP_TABLE;

/**
 *  Global access point for managing user's note data
 */

public class NoteDatabaseIml extends DatabaseImpl<NoteModel> {

    private static final String TAG = NoteDatabaseIml.class.getSimpleName();

    private SQLiteDatabase databaseWrite;
    private SQLiteDatabase databaseRead;

    private static NoteDatabaseIml instance;

    private NoteDatabaseIml() {}

    public static NoteDatabaseIml getInstance() {
        if (instance == null) {
            instance = new NoteDatabaseIml();
        }
        return instance;
    }

    @Override
    public void initDbImpl(Context context) {
        NoteDBHelper noteDBHelper = new NoteDBHelper(context);

        databaseWrite = noteDBHelper.getWritableDatabase();
        databaseRead = noteDBHelper.getReadableDatabase();

        initialized = true;
    }

    @Override
    public void clearDatabaseImpl() {
        super.clearDatabaseImpl();

        databaseWrite.execSQL(QUERY_DROP_TABLE);
    }

    @Override
    public int addRecordImpl(final NoteModel note) {
        super.addRecordImpl(note);

        Log.info(TAG, "addRecordImpl()");

        ContentValues values = new ContentValues();
        values.put(NotesEntry.COLUMN_NAME_NOTE, note.getNote());
        values.put(NotesEntry.COLUMN_NAME_TITLE, note.getTitle());
        values.put(NotesEntry.COLUMN_NAME_TIMESTAMP, note.getTime());

        long row = databaseWrite.insert(NotesEntry.TABLE_NAME, null, values);

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

        String selection = NotesEntry._ID + FLAG_SELECT;
        String[] selectionArgs = { String.valueOf(id) };

        int count = databaseWrite.delete(NotesEntry.TABLE_NAME, selection, selectionArgs);

        Log.info(TAG, "deleteRecordImpl(), deleted rows=" + count);

        return count > 0;
    }

    @Override
    public boolean updateRecordImpl(final String id, final NoteModel newData) {
        super.updateRecordImpl(id, newData);

        Log.info(TAG, "updateRecordImpl() id=" + id);

        ContentValues values = new ContentValues();
        values.put(NotesEntry.COLUMN_NAME_NOTE, newData.getNote());
        values.put(NotesEntry.COLUMN_NAME_TITLE, newData.getTitle());
        values.put(NotesEntry.COLUMN_NAME_TIMESTAMP, newData.getTime());

        int count = databaseWrite.update(NotesEntry.TABLE_NAME, values, NotesEntry._ID + FLAG_SELECT,
                new String[]{ String.valueOf(id) });

        if (count == 0) {
            Log.error(TAG, "updateRecordImpl(), error, no rows updated");
        }

        return !(count == 0);
    }

    @Override
    public NoteModel getRecordImpl(final String id) {
        super.getRecordImpl(id);

        return getRecord(id);
    }

    @Override
    public List<NoteModel> getRecordsImpl() {
        super.getRecordsImpl();

        List<NoteModel> notes = new ArrayList<>();

        Cursor c = databaseRead.rawQuery("SELECT * FROM " + NotesEntry.TABLE_NAME, null);

        while (c.moveToNext()) {
            NoteModel note = getNote(c);
            notes.add(note);
        }

        return notes;
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

        long count = DatabaseUtils.queryNumEntries(databaseRead, NotesEntry.TABLE_NAME);

        Log.info(TAG, "getRecordsCount(), count = " + count);

        return (int) count;
    }

    private NoteModel getRecord(final String id) {

        Log.info(TAG, "getRecord() id=" + id);

        String sortOrder = NotesEntry.COLUMN_NAME_TIMESTAMP + FLAG_ORDER;

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                NotesEntry._ID,
                NotesEntry.COLUMN_NAME_TITLE,
                NotesEntry.COLUMN_NAME_NOTE,
                NotesEntry.COLUMN_NAME_TIMESTAMP
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = NotesEntry._ID + FLAG_SELECT;
        String[] selectionArgs = { id };

        Cursor c = databaseRead.query(NoteTableModel.NotesEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);

        if (c.getCount() == 0) {
            Log.error(TAG, "getRecord(), no records found");
        } else {
            Log.info(TAG, "getRecord(), row count:" + c.getCount());
        }

        NoteModel note = null;

        if (c.moveToNext()) {
            note = getNote(c);
        }

        c.close();

        return note;
    }

    @SuppressLint("Range")
    private NoteModel getNote(Cursor c) {
        Log.info(TAG, "getNoteModel()");
        String id = c.getString(c.getColumnIndex(NotesEntry._ID));
        String title = c.getString(c.getColumnIndex(NotesEntry.COLUMN_NAME_TITLE));
        String note = c.getString(c.getColumnIndex(NotesEntry.COLUMN_NAME_NOTE));
        String time = c.getString(c.getColumnIndex(NotesEntry.COLUMN_NAME_TIMESTAMP));
        return new NoteModel(note, title, time, id);
    }

}
