package com.serhii.apps.notes.ui.view_model;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.serhii.apps.notes.database.NotesDatabase;
import com.serhii.apps.notes.database.UserNotesDatabase;
import com.serhii.apps.notes.ui.data_model.NoteModel;
import com.serhii.core.log.Log;
import com.serhii.core.security.impl.crypto.CryptoError;

import java.util.ArrayList;
import java.util.List;

/**
 *  View model for managing UI actions for storing and displaying User's note data
 */

public class NotesViewModel extends AndroidViewModel {

    private static final String TAG = "NotesVM";

    private final MutableLiveData<List<NoteModel>> notes = new MutableLiveData<>();
    private final MutableLiveData<CryptoError> errorState = new MutableLiveData<>();

    private final NotesDatabase<NoteModel> notesDatabaseProvider;

    public NotesViewModel(Application application) {
        super(application);

        notesDatabaseProvider = UserNotesDatabase.getInstance();
        notesDatabaseProvider.init(application.getApplicationContext());

        errorState.setValue(CryptoError.OK);
        notes.setValue(new ArrayList<NoteModel>());

        Log.info(TAG, "NotesViewModel(), initialization is finished");
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        notesDatabaseProvider.close();

        Log.info(TAG, "onCleared(), clean up is finished");
    }

    public LiveData<List<NoteModel>> getNotes() {
        return notes;
    }

    public boolean deleteNote(String index) {
        Log.info(TAG, "deleteNote()");

        boolean result = notesDatabaseProvider.deleteRecord(index);

        if (result) {
            updateData();
        }

        return result;
    }

    public boolean addNote(NoteModel noteModel) {
        Log.info(TAG, "addNote()");

        // Save data in database
        int result = notesDatabaseProvider.addRecord(noteModel);

        if (result != -1 && result != 0) {
            updateData();
            return true;
        }

        // Failed to save data
        return false;
    }

    public boolean updateNote(String index, NoteModel noteModel) {
        Log.info(TAG, "updateNote(), index = " + index);

        boolean result = notesDatabaseProvider.updateRecord(index, noteModel);

        if (result) {
            updateData();
        }

        return result;
    }

    public NoteModel getNote(String index) {
        Log.info(TAG, "getNote(), index = " + index);

        NoteModel data = notesDatabaseProvider.getRecord(index);

        return data;
    }

    public void updateData() {
        Log.info(TAG, "retrieveData(), load data");

        List<NoteModel> data = notesDatabaseProvider.getRecords();
        notes.setValue(data);
    }

    public LiveData<CryptoError> getErrorStateData() { return errorState; }

    public void resetErrorState() { }

}
