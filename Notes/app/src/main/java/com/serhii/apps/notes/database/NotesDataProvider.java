package com.serhii.apps.notes.database;

import android.content.Context;

import com.serhii.apps.notes.ui.data_model.NoteModel;
import com.serhii.core.log.Log;
import com.serhii.core.security.impl.crypto.CryptoError;
import com.serhii.core.utils.GoodUtils;

import java.util.List;

/**
 *  This class manages encryption / decryption data before store / retrieve data to database
 *
 *  It is a decorator around NotesDatabase class
 */

public class NotesDataProvider implements Database<NoteModel> {

    public static final String TAG = NotesDataProvider.class.getSimpleName();

    private final EncryptionHelper encryptionHelper;

    public NotesDataProvider(Context context) {
        encryptionHelper = new EncryptionHelper(context);
    }

    @Override
    public void init(Context context) {
        NotesDatabase.getInstance().init(context);
    }

    @Override
    public void clear() {
        NotesDatabase.getInstance().clear();
    }

    @Override
    public int addRecord(NoteModel uiData) {

        // Update time
        uiData.setTime(GoodUtils.currentTimeToString());

        NoteModel noteEnc = encryptionHelper.encrypt(uiData);

        if (noteEnc == null) {
            Log.error(TAG, "addRecord(), enc failed");
            return 0;
        }

        int index = NotesDatabase.getInstance().addRecord(noteEnc);

        if (index != -1) {
            encryptionHelper.saveMetaData(index);

            return index;
        }

        Log.error(TAG, "addRecord(), failed to add new record");

        return 0;
    }

    @Override
    public boolean deleteRecord(String id) {

        NoteModel noteModel = NotesDatabase.getInstance().getRecord(id);

        if (noteModel != null) {

            boolean success = NotesDatabase.getInstance().deleteRecord(id);

            if (success) {
                encryptionHelper.removeMetaData(Integer.parseInt(id));

                return true;
            }

        }

        return false;
    }

    @Override
    public boolean updateRecord(String id, NoteModel uiData) {

        // Update time
        uiData.setTime(GoodUtils.currentTimeToString());

        NoteModel noteEnc = encryptionHelper.encrypt(uiData);

        if (noteEnc == null) {
            Log.error(TAG, "updateNote(), enc failed");
            return false;
        }

        if (NotesDatabase.getInstance().updateRecord(id, noteEnc)) {
            encryptionHelper.saveMetaData(Integer.parseInt(id));
            return true;
        }

        Log.error(TAG, "updateNote(), failed to update record");

        return false;
    }

    @Override
    public NoteModel getRecord(String id) {

        NoteModel data = NotesDatabase.getInstance().getRecord(id);
        NoteModel decData = null;

        if (!data.isEmpty()) {
            decData = encryptionHelper.decrypt(data, Integer.parseInt(id));
        }

        return decData;
    }

    @Override
    public List<NoteModel> getRecords() {

        List<NoteModel> data = NotesDatabase.getInstance().getRecords();
        List<NoteModel> decData = null;

        if (!data.isEmpty()) {
            decData = encryptionHelper.decryptData(data);
        }

        return decData;
    }

    @Override
    public int getRecordsCount() {
        return NotesDatabase.getInstance().getRecordsCount();
    }

    @Override
    public void close() {
        NotesDatabase.getInstance().close();
    }

    public CryptoError getLastError() { return encryptionHelper.getLastError(); }

    public void resetErrors() { encryptionHelper.resetErrors(); }

}
