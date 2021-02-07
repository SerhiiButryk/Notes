package com.serhii.apps.notes.database.impl.base;

import android.content.Context;

import com.serhii.apps.notes.ui.data_model.NoteModel;

import java.util.List;

public abstract class DatabaseImpl<T> {

    public static final String FLAG_SELECT = "=?";
    public static final String FLAG_ORDER = " ASC";

    protected boolean initialized;

    public void initDbImpl(Context context) { checkIfInitialized(); }

    public void clearDatabaseImpl() { checkIfInitialized(); }

    public int addRecordImpl(final T uiData) { checkIfInitialized(); return -1; }

    public boolean deleteRecordImpl(final String id) { checkIfInitialized(); return false; }

    public boolean updateRecordImpl(final String id, final NoteModel newData) { checkIfInitialized(); return false; }

    public T getRecordImpl(final String id) { checkIfInitialized(); return  null; }

    public List<T> getRecordsImpl() { checkIfInitialized(); return null; }

    public void closeImpl() { checkIfInitialized(); }

    public int getRecordsCountImpl() { checkIfInitialized(); return 0; }

    protected void checkIfInitialized() {
        if (!initialized) {
            throw new IllegalStateException("Database was not initialized");
        }
    }

}
