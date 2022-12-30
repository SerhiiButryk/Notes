/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.database;

import android.content.Context;

import com.serhii.apps.notes.database.impl.EncryptionHelper;
import com.serhii.apps.notes.database.impl.NotesDatabaseIml;
import com.serhii.apps.notes.database.impl.database.DatabaseImpl;
import com.serhii.apps.notes.ui.data_model.NoteModel;
import com.serhii.core.log.Log;
import com.serhii.core.utils.GoodUtils;

import java.util.List;
import java.util.Map;

/**
 * Singleton implementation for user notes database.
 * Also this class adds encryption layer for note data.
 */

public class UserNotesDatabase implements NotesDatabase<NoteModel> {

    private static final String TAG = "UserNotesDatabase";
    private static final UserNotesDatabase INSTANCE = new UserNotesDatabase();

    private final DatabaseImpl impl;
    private EncryptionHelper encryptionHelper;

    private UserNotesDatabase() { impl = NotesDatabaseIml.getInstance(); }

    public static UserNotesDatabase getInstance() {
        return INSTANCE;
    }

    @Override
    public void init(Context context) {
        if (encryptionHelper == null) {
            encryptionHelper = new EncryptionHelper(context);
        }
        impl.initDbImpl(context);
    }

    @Override
    public void clear() {
        impl.clearDatabaseImpl();
    }

    @Override
    public int addRecord(final NoteModel uiData) {
        // Set last saved time for note
        uiData.setTime(GoodUtils.currentTimeToString());

        int count = impl.getRecordsCountImpl();
        Log.info(TAG, "addRecord(), current note count = " + count);

        // Id is an index of note raw in a table// We assume index always is count + 1
        uiData.setId(String.valueOf(count + 1));

        String noteEnc = encryptionHelper.encrypt(uiData);

        int index = impl.addRecordImpl(noteEnc);
        Log.info(TAG, "addRecord(), added new note with index = " + index);

        if (index != -1) {
            encryptionHelper.saveMetaData(index);
        } else {
            Log.error(TAG, "addRecord(), failed to add new record, returned index -1");
        }

        return index;
    }

    @Override
    public boolean deleteRecord(final String id) {
        boolean success = impl.deleteRecordImpl(id);
        Log.info(TAG, "deleteRecord(), id = " + id + ", result = " + success);
        return success;
    }

    @Override
    public boolean updateRecord(final String id, final NoteModel newData) {
        // Set last saved time for note
        newData.setTime(GoodUtils.currentTimeToString());
        // Set note id
        newData.setId(id);
        String noteEnc = encryptionHelper.encrypt(newData);
        // Save meta data
        if (noteEnc != null && !noteEnc.isEmpty()) {
            encryptionHelper.saveMetaData(Integer.parseInt(id));
        }
        return impl.updateRecordImpl(id, noteEnc);
    }

    @Override
    public NoteModel getRecord(final String id) {
        String data = impl.getRecordImpl(id);
        return encryptionHelper.decrypt(data, Integer.valueOf(id));
    }

    @Override
    public List<NoteModel> getRecords() {
        Map<Integer, String> data = impl.getRecordsImpl();
        return encryptionHelper.decrypt(data);
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
