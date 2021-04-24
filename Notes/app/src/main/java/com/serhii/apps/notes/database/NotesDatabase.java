package com.serhii.apps.notes.database;

import android.content.Context;

import com.serhii.apps.notes.database.impl.NoteDatabaseIml;
import com.serhii.apps.notes.database.impl.base.DatabaseImpl;
import com.serhii.apps.notes.ui.data_model.NoteModel;

import java.util.List;

/**
 * Thread safe singleton implementation
 */

class NotesDatabase implements Database<NoteModel> {

    private volatile static NotesDatabase INSTANCE;

    private final DatabaseImpl impl;

    private NotesDatabase() { impl = NoteDatabaseIml.getInstance(); }

    public static NotesDatabase getInstance() {
        if (INSTANCE == null) {
            synchronized (NotesDatabase.class) {
                if (INSTANCE == null)
                    INSTANCE = new NotesDatabase();
            }
        }
        return INSTANCE;
    }

    @Override
    public void init(Context context) {
        impl.initDbImpl(context);
    }

    @Override
    public void clear() {
        impl.clearDatabaseImpl();
    }

    @Override
    public int addRecord(final NoteModel uiData) {
        return impl.addRecordImpl(uiData);
    }

    @Override
    public boolean deleteRecord(final String id) {
        return impl.deleteRecordImpl(id);
    }

    @Override
    public boolean updateRecord(final String id, final NoteModel newData) {
        return impl.updateRecordImpl(id, newData);
    }

    @Override
    public NoteModel getRecord(final String id) {
        return (NoteModel) impl.getRecordImpl(id);
    }

    @Override
    public List<NoteModel> getRecords() {
        return impl.getRecordsImpl();
    }

    @Override
    public int getRecordsCount() {
        return impl.getRecordsCountImpl();
    }

    @Override
    public void close() {
        impl.closeImpl();
    }
}
