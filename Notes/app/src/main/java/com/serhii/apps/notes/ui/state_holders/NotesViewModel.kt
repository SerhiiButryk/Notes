/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.state_holders

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.serhii.apps.notes.activities.SettingsActivity
import com.serhii.apps.notes.common.App.BACKGROUND_DISPATCHER
import com.serhii.apps.notes.control.backup.BackupManager
import com.serhii.apps.notes.database.UserNotesDatabase
import com.serhii.apps.notes.repository.NotesRepository
import com.serhii.apps.notes.ui.data_model.NoteModel
import com.serhii.apps.notes.ui.search.search
import com.serhii.core.log.Log
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 *  View model for managing UI state and business logic of NotesViewActivity
 */
class NotesViewModel : AppViewModel() {

    // UI state observable data
    private val _uiState = MutableStateFlow(BaseUIState())
    val uiState: StateFlow<BaseUIState> = _uiState

    // This is the result of a text search in user's note items
    private val searchResults = MutableLiveData<List<NoteModel>>()

    // List or grid view mode for the Note View screen
    private val displayNoteMode = MutableLiveData<Int>()

    // During rotation we want to save any data which User entered
    // We cannot simply save it to database and then update before rotations
    // because auto save is not supported yet. So this is a kind of work-around
    var cachedNote: NoteModel? = null

    ///////////////////////////// UI State class /////////////////////////////

    open class BaseUIState

    class NotesMainUIState(val notes: List<NoteModel> = emptyList()) : BaseUIState()

    class NotesEditorUIState(val note: NoteModel = NoteModel()) : BaseUIState()

    //////////////////////////////////////////////////////////////////////////

    fun getDisplayNoteMode(): LiveData<Int> = displayNoteMode

    private val notesRepository: NotesRepository = NotesRepository()

    private val backupDataObserver: Observer<Boolean> =
        Observer { shouldUpdateData ->
            if (shouldUpdateData) {
                Log.info(TAG, "onChanged() got data change event, retrieving new data")
                updateNotesData()
                BackupManager.onDataUpdated()
            }
        }

    init {
        _uiState.value = NotesMainUIState()
        // Subscribe for updates from backup manager
        BackupManager.getUpdateDataFlagData().observeForever(backupDataObserver)
        Log.info(TAG, "NotesViewModel(), instance is created")
    }

    override fun initViewModel(context: Context) {
        super.initViewModel(context)
        // Init database
        UserNotesDatabase.init(context.applicationContext)
    }

    override fun onCleared() {
        super.onCleared()
        notesRepository.close()
        BackupManager.getUpdateDataFlagData().removeObserver(backupDataObserver)
        Log.info(TAG, "onCleared()")
    }

    fun setDisplayNoteMode(newMode: Int) {
        displayNoteMode.value = newMode
    }

    fun deleteNote(index: String?) {
        viewModelScope.launch(BACKGROUND_DISPATCHER) {
            Log.info(TAG, "deleteNote()")

            val result = if (index.isNullOrEmpty()) {
                Log.info(TAG, "deleteNote() empty")
                false
            } else {
                notesRepository.delete(index)
            }

            val notes = notesRepository.getAll()

//            uiState.postValue(NotesUIState(currentNotes = notes, actionId = ACTION_DELETED, success = result))
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
            val success = (noteid != -1 && noteid != 0) || result

//            uiState.postValue(NotesUIState(notes, noteid, ACTION_SAVE, success))
        }
    }

    fun getNote(index: String): NoteModel {
        Log.info(TAG, "getNote(), index = $index")
        return notesRepository.get(index)
    }

    fun updateNotesData() {
        viewModelScope.launch(BACKGROUND_DISPATCHER) {
            Log.info(TAG, "updateNotesData()")
            val notes = notesRepository.getAll()
//            _uiState.value = NotesMainUIState(notes)
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

    fun openNoteEditorUI(id: Int = 0) {
        _uiState.value = NotesEditorUIState()
    }

    fun navigateBack(): Boolean {
        val currentUIState = _uiState.value

        if (currentUIState is NotesEditorUIState) {
            // Go to previous screen
            _uiState.value = NotesMainUIState()
            return true
        }

        return false
    }

    fun openSettings(activity: Activity) {
        activity.startActivity(Intent(activity, SettingsActivity::class.java))
    }

    companion object {
        private const val TAG = "NotesViewModel"

        val ACTION_NONE = -1
        val ACTION_SAVE = 1
        val ACTION_DELETED = 2
    }
}