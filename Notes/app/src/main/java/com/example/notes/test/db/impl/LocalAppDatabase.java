package com.example.notes.test.db.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.notes.test.db.impl.base.AppDataBaseImpl;
import com.example.notes.test.db.impl.base.DBCypher;
import com.example.notes.test.db.impl.base.NoteDBHelper;
import com.example.notes.test.db.impl.model.DbNoteTableModel;
import com.example.notes.test.ui.data_model.NoteModel;
import com.example.core.common.log.Log;
import com.example.core.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.example.notes.test.db.impl.model.DbNoteTableModel.*;

/**
 *  Global access point for user's note records db
 */

public class LocalAppDatabase extends AppDataBaseImpl {

    private static final String TAG = "LocalAppDatabase";

    private static final int FLAG_GET_ALL_RECORDS = -11;
    private static final String FLAG_SELECT = " = ?";
    private static final String FLAG_ORDER = " ASC";

    private SQLiteDatabase databaseWrite;
    private SQLiteDatabase databaseRead;

    private static LocalAppDatabase instance;

    private DBCypher dbCypher;

    private LocalAppDatabase() {}

    public static LocalAppDatabase getInstance() {
        if (instance == null) {
            instance = new LocalAppDatabase();
        }
        return instance;
    }

    @Override
    public void initDbImpl(Context context) {
        NoteDBHelper noteDBHelper = new NoteDBHelper(context);

        databaseWrite = noteDBHelper.getWritableDatabase();
        databaseRead = noteDBHelper.getReadableDatabase();

        initialized = true;
        dbCypher = new DBCypher(context);
    }

    @Override
    public void clearDatabaseImpl() {
        super.clearDatabaseImpl();

        databaseWrite.execSQL(QUERY_DROP_TABLE);
    }

    @Override
    public boolean addRecordImpl(final NoteModel uiData) {
        super.addRecordImpl(uiData);

        Log.info(TAG, " addRecordImpl( " + uiData.getNote() + "\n" + uiData.getNoteTitle() + ")\n");

        NoteModel noteModel = dbCypher.encrypt(uiData, DBCypher.ADD_MODE);

        if (noteModel == null)
            return false;

        ContentValues values = new ContentValues();
        values.put(NotesEntry.COLUMN_NAME_NOTE, noteModel.getNote());
        values.put(NotesEntry.COLUMN_NAME_TITLE, noteModel.getNoteTitle());

        long idRow = databaseWrite.insert(NotesEntry.TABLE_NAME, null, values);

        boolean success = true;

        if (idRow == -1) {
            success = false;

            Log.error(TAG, "failed to insert values to databaseWrite");
        } else {

            dbCypher.saveInitializeVector(idRow - 1); // Start from 0

            Log.error(TAG, "cashed iv");
        }

        return success;
    }

    @Override
    public void deleteRecordImpl(int id) {
        super.deleteRecordImpl(id);

        Log.info(TAG, " deleteRecordImpl(" + id + ")");

        String selection = NotesEntry._ID + FLAG_SELECT;
        String[] selectionArgs = { String.valueOf(id + 1) }; // starts from 1

        int count = databaseWrite.delete(NotesEntry.TABLE_NAME, selection, selectionArgs);

        Log.info(TAG, "is deleted " + count + " rows with key: " + id);
    }

    @Override
    public boolean updateRecordImpl(final int id, final NoteModel uiData) {
        super.updateRecordImpl(id, uiData);

        Log.info(TAG, " updateRecordImpl(" + id + ")" + "New values "  + uiData.getNoteTitle() + " " + uiData.getNote() + "\n");

        NoteModel noteModel = dbCypher.encrypt(uiData, id);

        if (noteModel == null)
            return false;

        ContentValues values = new ContentValues();
        values.put(NotesEntry.COLUMN_NAME_NOTE, noteModel.getNote());
        values.put(NotesEntry.COLUMN_NAME_TITLE, noteModel.getNoteTitle());

        int count = databaseWrite.update(NotesEntry.TABLE_NAME, values, NotesEntry._ID + " = ?",
                new String[]{ String.valueOf(id + 1) }); // starts from 1

        boolean success = true;

        if (count == 0) {
            success = false;
            Log.error(TAG, "NO ROWS UPDATED");
        }

        return success;
    }

    @Override
    public NoteModel getRecordImpl(final int id) {
        super.getRecordImpl(id);

        List<NoteModel> data = getRecords(id);

        if (!data.isEmpty()) {
            return data.get(0);
        }

        return null;
    }

    @Override
    public List<NoteModel> getRecordsImpl() {
        super.getRecordsImpl();

        return getRecords(FLAG_GET_ALL_RECORDS);
    }

    @Override
    public void closeImpl() {
        super.closeImpl();

        databaseWrite.close();
        databaseRead.close();
    }

    private List<NoteModel> getRecords(final int id) {

        Log.info(TAG, "getRecordsImpl(" + id + ")");

        String sortOrder = NotesEntry.COLUMN_TIMESTAMP + FLAG_ORDER;

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                NotesEntry._ID,
                NotesEntry.COLUMN_NAME_TITLE,
                NotesEntry.COLUMN_NAME_NOTE
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = null;
        ArrayList<String> selectionArgs = new ArrayList<>();

        if (id != FLAG_GET_ALL_RECORDS) {
            selection = NotesEntry._ID + FLAG_SELECT;
            selectionArgs.add(String.valueOf(id + 1)); // Start from 1 ID
        }

        Cursor c = databaseRead.query(DbNoteTableModel.NotesEntry.TABLE_NAME,
                projection,
                selection,
                CollectionUtils.getConvertedArray(selectionArgs),
                null,
                null,
                sortOrder);

        if (c.getCount() == 0) {
            Log.error(TAG, "NO RECORDS FOUND ");
        } else {
            Log.info(TAG, "ROW COUNT : " + c.getCount());
        }

        List<NoteModel> noteValuesList = new ArrayList<>();

        while (c.moveToNext()) {

            long localId = (id == FLAG_GET_ALL_RECORDS) ? c.getPosition() : id;

            NoteModel note = getNoteModel(c, localId);

            if (note != null) {
                noteValuesList.add(note);
            }

            // DEBUG ONLY
            // Log.info(TAG, "ROW : " + note.getNoteTitle() + " \t" + note.getNote());
        }

        c.close();

        return noteValuesList;
    }

    private NoteModel getNoteModel(Cursor c, long id) {
        Log.info(TAG, "getNoteModel() ID " + c.getString(c.getColumnIndex(NotesEntry._ID)));

        String title = c.getString(c.getColumnIndex(NotesEntry.COLUMN_NAME_TITLE));
        String note = c.getString(c.getColumnIndex(NotesEntry.COLUMN_NAME_NOTE));

        return dbCypher.decrypt(new NoteModel(note, title), id);
    }

}
