/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.view_model

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.serhii.apps.notes.control.backup.BackupManager
import com.serhii.apps.notes.control.preferences.PreferenceManager.getNoteDisplayModePref
import com.serhii.apps.notes.database.UserNotesDatabase
import com.serhii.apps.notes.ui.data_model.NoteModel
import com.serhii.apps.notes.repository.NotesRepository
import com.serhii.apps.notes.repository.DataChangedListener
import com.serhii.apps.notes.ui.search.search
import com.serhii.core.log.Log
import com.serhii.core.log.Log.Companion.info
import com.serhii.core.security.impl.crypto.CryptoError
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * View model for managing UI state of NotesViewActivity
 *
 * Responsibilities:
 * 1) Save/Retrieve data to a database
 * 2) Hold data during config changes
 * 3) Access and update the UI state
 */
class NotesViewModel(application: Application) : AndroidViewModel(application), DataChangedListener {

    // User's notes list
    private val notes = MutableLiveData<List<NoteModel>>()

    // This is the result of a text search in user's note items
    private val searchResults = MutableLiveData<List<NoteModel>>()

    private val displayNoteMode = MutableLiveData<Int>()

    fun getDisplayNoteMode(): LiveData<Int> = displayNoteMode

    var cachedUserNotes: List<NoteModel>? = null
        private set

    private val notesRepository: NotesRepository = NotesRepository(this)

    private val backupDataObserver: Observer<Boolean> =
        Observer { shouldUpdateData ->
            if (shouldUpdateData) {
                info(TAG, "onChanged() got data change event, retrieving new data")
                updateData()
                BackupManager.onDataUpdated()
            }
        }

    init {
        // Init database
        UserNotesDatabase.init(application)

        notes.value = ArrayList()

        val mode = getNoteDisplayModePref(application.applicationContext)
        displayNoteMode.value = mode

        // Subscribe for updates from backup manager
        BackupManager.getUpdateDataFlagData().observeForever(backupDataObserver)

        info(TAG, "NotesViewModel(), instance is created")
    }

    override fun onCleared() {
        super.onCleared()
        notesRepository.close()
        BackupManager.getUpdateDataFlagData().removeObserver(backupDataObserver)
        info(TAG, "onCleared()")
    }

    fun getNotes(): LiveData<List<NoteModel>> {
        return notes
    }

    fun cacheUserNote(userListCached: List<NoteModel>?) {
        cachedUserNotes = userListCached
    }

    fun onBackNavigation() {
        // Clear cached data. We don't need it anymore.
        cachedUserNotes = null
    }

    fun setDisplayNoteMode(newMode: Int) {
        displayNoteMode.value = newMode
    }

    fun deleteNote(index: String): Boolean {
        info(TAG, "deleteNote()")
        return notesRepository.delete(index)
    }

    fun addNote(noteModel: NoteModel): Boolean {
        info(TAG, "addNote()")
        return notesRepository.add(noteModel)
    }

    fun updateNote(index: String, noteModel: NoteModel): Boolean {
        info(TAG, "updateNote(), index = $index")
        return notesRepository.update(index, noteModel)
    }

    fun getNote(index: String): NoteModel {
        info(TAG, "getNote(), index = $index")
        return notesRepository.get(index)
    }

    override fun updateData() {
        viewModelScope.launch(Dispatchers.Default) {
            Log.info(TAG, "updateData(), get all records")
            val data = notesRepository.getAll()
            notes.postValue(data)
        }
    }

    fun getSearchResults(): LiveData<List<NoteModel>> {
        return searchResults
    }

    fun performSearch(query: String, noteForSearch: NoteModel? = null) {
        viewModelScope.launch(defaultDispatcher + CoroutineName("NoteSearch")) {
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
        // For coroutines
        val defaultDispatcher = Dispatchers.Default;
    }
}