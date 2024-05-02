/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.serhii.apps.notes.common.App.BACKGROUND_DISPATCHER
import com.serhii.apps.notes.control.backup.BackupManager
import com.serhii.apps.notes.control.preferences.PreferenceManager.getNoteDisplayModePref
import com.serhii.apps.notes.database.UserNotesDatabase
import com.serhii.apps.notes.ui.data_model.NoteModel
import com.serhii.apps.notes.repository.NotesRepository
import com.serhii.apps.notes.ui.search.search
import com.serhii.core.log.Log
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.launch

/**
 * View model for managing UI state of NotesViewActivity
 *
 * Responsibilities:
 * 1) Save/Retrieve data to/from a database
 * 2) Hold data during config changes
 * 3) Access and update the UI state
 */
class NotesViewModel(application: Application) : AndroidViewModel(application) {

    // UI state observable data
    private val uiState = MutableLiveData<NotesUIState>()

    // This is the result of a text search in user's note items
    private val searchResults = MutableLiveData<List<NoteModel>>()

    private val displayNoteMode = MutableLiveData<Int>()

    // UI State class
    class NotesUIState(val currentNotes: List<NoteModel> = emptyList(),
                       val lastNoteId: Int = -1,
                       val actionId: Int = ACTION_NONE,
                       val success: Boolean = false,
                       // We have situations when the last state can be delivered again.
                       // For example, user opened Editor Fragment, saved/updated note, then closed it
                       // and opened a new Editor Fragment to create a new note. In this case Editor Fragment receives
                       // the last UI state which is not okay. Added this to work-around this case.
                       var processed: Boolean = false)

    fun getDisplayNoteMode(): LiveData<Int> = displayNoteMode

    private val notesRepository: NotesRepository = NotesRepository()

    private val backupDataObserver: Observer<Boolean> =
        Observer { shouldUpdateData ->
            if (shouldUpdateData) {
                Log.info(TAG, "onChanged() got data change event, retrieving new data")
                updateAllNotes()
                BackupManager.onDataUpdated()
            }
        }

    init {
        // Init database
        UserNotesDatabase.init(application)

        uiState.value = NotesUIState()

        val mode = getNoteDisplayModePref(application.applicationContext)
        displayNoteMode.value = mode

        // Subscribe for updates from backup manager
        BackupManager.getUpdateDataFlagData().observeForever(backupDataObserver)

        Log.info(TAG, "NotesViewModel(), instance is created")
    }

    override fun onCleared() {
        super.onCleared()
        notesRepository.close()
        BackupManager.getUpdateDataFlagData().removeObserver(backupDataObserver)
        Log.info(TAG, "onCleared()")
    }

    fun getUIState(): LiveData<NotesUIState> {
        return uiState
    }

    fun setDisplayNoteMode(newMode: Int) {
        displayNoteMode.value = newMode
    }

    fun deleteNote(index: String?) {
        viewModelScope.launch(BACKGROUND_DISPATCHER) {
            Log.info(TAG, "deleteNote()")
            if (index.isNullOrEmpty()) {
                Log.info(TAG, "deleteNote() empty")
                uiState.postValue(NotesUIState(actionId = ACTION_DELETED, success = false))
            } else {
                val result = notesRepository.delete(index)
                uiState.postValue(NotesUIState(actionId = ACTION_DELETED, success = result))
            }
        }
    }

    private fun addNote(noteModel: NoteModel): Int {
        Log.info(TAG, "addNote()")
        return notesRepository.add(noteModel)
    }

    fun saveNote(index: String?, noteModel: NoteModel) {
        viewModelScope.launch(BACKGROUND_DISPATCHER) {
            Log.info(TAG, "saveNote(), index = $index")
            var noteid = -1
            var result = false
            // Update note by passed index if exists or add a new one
            if (index.isNullOrEmpty()) {
                noteid = addNote(noteModel)
            } else {
                val note = notesRepository.get(index)
                if (!note.isEmpty) {
                    result = notesRepository.update(index, noteModel)
                } else {
                    noteid = addNote(noteModel)
                }
            }

            val notes = notesRepository.getAll()

            // Update ui state
            if ((noteid != -1 && noteid != 0) || result) {
                uiState.postValue(NotesUIState(notes, noteid, ACTION_SAVE, true))
            } else {
                uiState.postValue(NotesUIState(notes, noteid, ACTION_SAVE, false))
            }
        }
    }

    fun getNote(index: String): NoteModel {
        Log.info(TAG, "getNote(), index = $index")
        return notesRepository.get(index)
    }

    fun updateAllNotes() {
        viewModelScope.launch(BACKGROUND_DISPATCHER) {
            Log.info(TAG, "updateAllNotes()")
            val notes = notesRepository.getAll()
            uiState.postValue(NotesUIState(notes))
        }
    }

    fun getSearchResults(): LiveData<List<NoteModel>> {
        return searchResults
    }

    fun performSearch(query: String, noteForSearch: NoteModel? = null) {
        viewModelScope.launch(BACKGROUND_DISPATCHER + CoroutineName("NoteSearch")) {
            Log.info(message = "performSearch()")
            val noteForSearchList: List<NoteModel> = if (noteForSearch != null) {
                listOf(noteForSearch)
            } else {
                notesRepository.getAll()
            }
            // Start a search
            search(query, noteForSearchList, searchResults)
        }
    }

    companion object {
        private const val TAG = "NotesViewModel"

        val ACTION_NONE = -1
        val ACTION_SAVE = 1
        val ACTION_DELETED = 2
    }
}