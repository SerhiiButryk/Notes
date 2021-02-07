package com.serhii.apps.notes.ui.view_model;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.serhii.core.log.Log;
import com.serhii.core.utils.GoodUtils;
import com.serhii.apps.notes.control.EventService;
import com.serhii.apps.notes.database.NotesDatabase;
import com.serhii.apps.notes.ui.data_model.NoteModel;
import com.serhii.apps.notes.ui.utils.EncryptionHelper;

import java.util.List;

public class NotesViewModel extends ViewModel {

    private static final String TAG = NotesViewModel.class.getSimpleName();

    private MutableLiveData<List<NoteModel>> notes = new MutableLiveData<>();
    private EncryptionHelper encryptionHelper;

    public NotesViewModel(Context applicationContext) {
        encryptionHelper = new EncryptionHelper(applicationContext);

        NotesDatabase.getInstance().init(applicationContext);

        retrieveData();
        Log.info(TAG, "NotesViewModel(), created");
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        NotesDatabase.getInstance().close();

        Log.info(TAG, "onCleared()");
    }

    public LiveData<List<NoteModel>> getNotes() {
        return notes;
    }

    public boolean deleteNote(String index) {
        Log.info(TAG, "deleteNote()");

        boolean success = false;

        NoteModel noteModel = NotesDatabase.getInstance().getRecord(index);

        if (noteModel != null) {

            success = NotesDatabase.getInstance().deleteRecord(index);

            if (success) {
                encryptionHelper.removeMetaData(Integer.parseInt(index));

                retrieveData();
            }

        }

        return success;
    }

    public boolean addNote(NoteModel noteModel) {
        Log.info(TAG, "addNote()");

        // Update time
        noteModel.setTime(GoodUtils.currentTimeToString());

        NoteModel noteEnc = encryptionHelper.encrypt(noteModel);

        if (noteEnc == null) {
            Log.error(TAG, "addNote(), dec failed");
            // Need to request keystore unlock
            EventService.getInstance().onUnlockKeystore();
            return false;
        }

        int index = NotesDatabase.getInstance().addRecord(noteEnc);

        if (index != -1) {
            encryptionHelper.saveMetaData(index);

            retrieveData();

            return true;
        }

        return false;
    }

    public boolean updateNote(String index, NoteModel noteModel) {
        Log.info(TAG, "updateNote()");

        // Update time
        noteModel.setTime(GoodUtils.currentTimeToString());

        NoteModel noteEnc = encryptionHelper.encrypt(noteModel);

        if (noteEnc == null) {
            Log.error(TAG, "updateNote(), dec failed");
            // Need to request keystore unlock
            EventService.getInstance().onUnlockKeystore();
            return false;
        }

        if (NotesDatabase.getInstance().updateRecord(index, noteEnc)) {
            encryptionHelper.saveMetaData(Integer.parseInt(index));

            retrieveData();

            return true;
        }

        return false;
    }

    public NoteModel getNote(String index) {
        NoteModel data = NotesDatabase.getInstance().getRecord(index);
        NoteModel decData = null;

        if (data != null) {
            decData = encryptionHelper.decrypt(data, Integer.parseInt(index));
            if (decData == null) {
                Log.error(TAG, "getNote(), dec failed");
                // Need to request keystore unlock
                EventService.getInstance().onUnlockKeystore();
            }
        }

        return decData;
    }

    public void retrieveData() {
        Log.info(TAG, "loadData(), load and decrypt");

        List<NoteModel> data = NotesDatabase.getInstance().getRecords();
        List<NoteModel> decData = encryptionHelper.decodeData(data);

        if (!data.isEmpty() && decData.isEmpty()) {
            Log.error(TAG, "loadData(), dec failed");
            // Need to request keystore unlock
            EventService.getInstance().onUnlockKeystore();
        }

        notes.setValue(decData);
    }


}
