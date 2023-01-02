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
 *  View model for managing UI data for User notes
 *
 *  Responsibilities:
 *  1) Save data to Database
 *  2) Get latest data from Database
 *  3) Survive data during config changes
 *  3) Close Database when User is done with notes
 */

public class NotesViewModel extends AndroidViewModel implements NotifyUpdateData {

    private static final String TAG = "NotesViewModel";

    private final MutableLiveData<List<NoteModel>> notes = new MutableLiveData<>();
    private final MutableLiveData<CryptoError> errorState = new MutableLiveData<>();
    private List<NoteModel> cachedUserNotes;
    private final NotesRepository notesRepository;

    public NotesViewModel(Application application) {
        super(application);
        notesRepository = new NotesRepository(application, this);
        errorState.setValue(CryptoError.OK);
        notes.setValue(new ArrayList<>());
        Log.info(TAG, "NotesViewModel(), initialization is finished");
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        notesRepository.close();
        Log.info(TAG, "onCleared(), clean up is finished");
    }

    public LiveData<List<NoteModel>> getNotes() {
        return notes;
    }

    public LiveData<CryptoError> getErrorStateData() { return errorState; }

    public void resetErrorState() { /* no-op */ }

    public void cacheUserNote(List<NoteModel> userListCached) {
        cachedUserNotes = userListCached;
    }

    public List<NoteModel> getCachedUserNotes() {
        return cachedUserNotes;
    }

    public void onBackNavigation() {
        // Clear cached data. Not need it anymore.
        cachedUserNotes = null;
    }

    public boolean deleteNote(String index) {
        Log.info(TAG, "deleteNote()");
        return notesRepository.delete(index);
    }

    public boolean addNote(NoteModel noteModel) {
        Log.info(TAG, "addNote()");
        return notesRepository.add(noteModel);
    }

    public boolean updateNote(String index, NoteModel noteModel) {
        Log.info(TAG, "updateNote(), index = " + index);
        return notesRepository.update(index, noteModel);
    }

    public NoteModel getNote(String index) {
        Log.info(TAG, "getNote(), index = " + index);
        return notesRepository.get(index);
    }

    @Override
    public void updateData() {
        Log.info(TAG, "retrieveData(), load data");
        List<NoteModel> data = notesRepository.getAll();
        notes.setValue(data);
    }

}
