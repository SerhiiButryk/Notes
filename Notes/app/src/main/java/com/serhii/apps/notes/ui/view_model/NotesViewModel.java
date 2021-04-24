package com.serhii.apps.notes.ui.view_model;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.serhii.apps.notes.control.EventService;
import com.serhii.apps.notes.database.NotesDataProvider;
import com.serhii.apps.notes.ui.data_model.NoteModel;
import com.serhii.core.log.Log;
import com.serhii.core.security.impl.crypto.CryptoError;

import java.util.ArrayList;
import java.util.List;

public class NotesViewModel extends ViewModel {

    private static final String TAG = NotesViewModel.class.getSimpleName();

    private MutableLiveData<List<NoteModel>> notes = new MutableLiveData<>();
    private MutableLiveData<CryptoError> errorState = new MutableLiveData<>();

    private NotesDataProvider notesDataProvider;

    public NotesViewModel(Context applicationContext) {
        notesDataProvider = new NotesDataProvider(applicationContext);
        notesDataProvider.init(applicationContext);

        errorState.setValue(CryptoError.OK);
        notes.setValue(new ArrayList<NoteModel>());

        Log.info(TAG, "NotesViewModel(), instance is created");
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        notesDataProvider.close();

        Log.info(TAG, "onCleared()");
    }

    public LiveData<List<NoteModel>> getNotes() {
        return notes;
    }

    public boolean deleteNote(String index) {
        Log.info(TAG, "deleteNote()");

        boolean success = notesDataProvider.deleteRecord(index);

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

        int result = notesDataProvider.addRecord(noteModel);

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

        boolean result = notesDataProvider.updateRecord(index, noteModel);

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

        NoteModel data = notesDataProvider.getRecord(index);

        if (handleError()) {
            return null;
        }

        return data;
    }

    public void updateData() {
        Log.info(TAG, "retrieveData(), load data");

        List<NoteModel> data = notesDataProvider.getRecords();

        if (!handleError() && data != null) {
            Log.info(TAG, "updateData(), data is updated");
            notes.setValue(data);
        }

    }

    public LiveData<CryptoError> getErrorStateData() { return errorState; }

    public void resetErrorState() { notesDataProvider.resetErrors(); }

    private boolean handleError() {
        if (notesDataProvider.getLastError() == CryptoError.USER_NOT_AUTHORIZED) {
            Log.error(TAG, "handleError(), auth error");
            // Need to request keystore unlock
            errorState.setValue(CryptoError.USER_NOT_AUTHORIZED);
            return true;
        }
        return false;
    }


}
