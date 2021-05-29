package com.serhii.apps.notes.ui.view_model;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.serhii.apps.notes.database.NotesDatabaseProvider;
import com.serhii.apps.notes.ui.data_model.NoteModel;
import com.serhii.core.log.Log;
import com.serhii.core.security.impl.crypto.CryptoError;

import java.util.ArrayList;
import java.util.List;

public class NotesViewModel extends ViewModel {

    private static final String TAG = NotesViewModel.class.getSimpleName();

    private MutableLiveData<List<NoteModel>> notes = new MutableLiveData<>();
    private MutableLiveData<CryptoError> errorState = new MutableLiveData<>();

    private NotesDatabaseProvider notesDatabaseProvider;

    public NotesViewModel(Context applicationContext) {
        notesDatabaseProvider = new NotesDatabaseProvider(applicationContext);
        notesDatabaseProvider.init(applicationContext);

        errorState.setValue(CryptoError.OK);
        notes.setValue(new ArrayList<NoteModel>());

        Log.info(TAG, "NotesViewModel(), instance is created");
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        notesDatabaseProvider.close();

        Log.info(TAG, "onCleared()");
    }

    public LiveData<List<NoteModel>> getNotes() {
        return notes;
    }

    public boolean deleteNote(String index) {
        Log.info(TAG, "deleteNote()");

        boolean success = notesDatabaseProvider.deleteRecord(index);

        if (handleError()) {
            return false;
        }

        if (success) {
            updateData();
        }

        return success;
    }

    public boolean addNote(NoteModel noteModel) {
        Log.info(TAG, "addNote()");

        int result = notesDatabaseProvider.addRecord(noteModel);

        if (handleError()) {
            return false;
        }

        if (result != -1 && result != 0) {
            updateData();
            return true;
        }

        return false;
    }

    public boolean updateNote(String index, NoteModel noteModel) {
        Log.info(TAG, "updateNote(), index = " + index);

        boolean result = notesDatabaseProvider.updateRecord(index, noteModel);

        if (handleError()) {
            return false;
        }

        if (result) {
            updateData();
        }

        return result;
    }

    public NoteModel getNote(String index) {
        Log.info(TAG, "getNote(), index = " + index);

        NoteModel data = notesDatabaseProvider.getRecord(index);

        if (handleError()) {
            return null;
        }

        return data;
    }

    public void updateData() {
        Log.info(TAG, "retrieveData(), load data");

        List<NoteModel> data = notesDatabaseProvider.getRecords();

        if (!handleError() && data != null) {
            Log.info(TAG, "updateData(), data is updated");
            notes.setValue(data);
        } else {
            notes.setValue(new ArrayList<NoteModel>());
        }

    }

    public LiveData<CryptoError> getErrorStateData() { return errorState; }

    public void resetErrorState() { notesDatabaseProvider.resetErrors(); }

    private boolean handleError() {
        if (notesDatabaseProvider.getLastError() == CryptoError.USER_NOT_AUTHORIZED) {
            Log.error(TAG, "handleError(), auth error");
            // Need to request keystore unlock
            errorState.setValue(CryptoError.USER_NOT_AUTHORIZED);
            return true;
        }
        return false;
    }


}
