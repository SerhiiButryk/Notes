package com.example.notes.test.db.impl;

import android.content.Context;

import com.example.notes.test.ui.data_model.NoteModel;

import java.util.List;

public abstract class AppDataBaseImpl {

    protected boolean initialized;

    public void initDbImpl(Context context) { checkIfInitialized(); }

    public void clearDatabaseImpl() { checkIfInitialized(); }

    public boolean addRecordImpl(final NoteModel uiData) { checkIfInitialized(); return false; }

    public void deleteRecordImpl(int id) { checkIfInitialized(); }

    public boolean updateRecordImpl(final int id, final NoteModel uiData) { checkIfInitialized(); return false; }

    public NoteModel getRecordImpl(final int id) { checkIfInitialized(); return  null; }

    public List<NoteModel> getRecordsImpl() { checkIfInitialized(); return null; }

    public void closeImpl() { checkIfInitialized(); }

    protected void checkIfInitialized() {
        if (!initialized) {
            throw new IllegalStateException("Database was not initialized");
        }
    }

}
