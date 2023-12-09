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
import com.serhii.apps.notes.ui.repository.NotesRepository
import com.serhii.apps.notes.ui.repository.DataChangedListener
import com.serhii.apps.notes.ui.search.SearchInfo
import com.serhii.apps.notes.ui.search.search
import com.serhii.core.log.Log
import com.serhii.core.log.Log.Companion.info
import com.serhii.core.security.impl.crypto.CryptoError
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * View model for managing UI data of user's notes
 *
 * Responsibilities:
 * 1) Save data to a database
 * 2) Get latest data from a database
 * 3) Hold data during config changes
 * 4) Access and update the UI data
 */
class NotesViewModel(application: Application) : AndroidViewModel(application), DataChangedListener {
    // User's notes list
    private val notes = MutableLiveData<List<NoteModel>>()
    // This is the result of a text search in user's note items
    private val searchResults = MutableLiveData<List<NoteModel>>()
    private val errorState = MutableLiveData<CryptoError>()
    private val displayNoteMode = MutableLiveData<Int>()

    fun getDisplayNoteMode(): LiveData<Int> = displayNoteMode

    var cachedUserNotes: List<NoteModel>? = null
        private set

    private val notesRepository: NotesRepository = NotesRepository(this)

    private val backupDataObserver: Observer<Boolean> =
        Observer { shouldUpdateData ->
            if (shouldUpdateData) {
                info(TAG, "onChanged() got data change event, retrieving new data")
                updateData(application)
                BackupManager.onDataUpdated()
            }
        }

    init {
        // Init database
        UserNotesDatabase.init(application)

        errorState.value = CryptoError.OK
        notes.value = ArrayList()

        val mode = getNoteDisplayModePref(application.applicationContext)
        displayNoteMode.value = mode

        // Subscribe for data updates from backup manager
        BackupManager.getUpdateDataFlagData().observeForever(backupDataObserver)

        info(TAG, "NotesViewModel(), initialization is finished")
    }

    override fun onCleared() {
        super.onCleared()
        notesRepository.close()
        BackupManager.getUpdateDataFlagData().removeObserver(backupDataObserver)
        info(TAG, "onCleared(), clean up is finished")
    }

    fun getNotes(): LiveData<List<NoteModel>> {
        return notes
    }

    fun resetErrorState() { /* no-op */
    }

    fun cacheUserNote(userListCached: List<NoteModel>?) {
        cachedUserNotes = userListCached
    }

    fun onBackNavigation() {
        // Clear cached data. Don't need it anymore.
        cachedUserNotes = null
    }

    fun setDisplayNoteMode(newMode: Int) {
        displayNoteMode.value = newMode
    }

    fun deleteNote(index: String, context: Context): Boolean {
        info(TAG, "deleteNote()")
        return notesRepository.delete(index, context)
    }

    fun addNote(noteModel: NoteModel, context: Context): Boolean {
        info(TAG, "addNote()")
        return notesRepository.add(noteModel, context)
    }

    fun updateNote(index: String, noteModel: NoteModel, context: Context): Boolean {
        info(TAG, "updateNote(), index = $index")
        return notesRepository.update(index, noteModel, context)
    }

    fun getNote(index: String, context: Context): NoteModel {
        info(TAG, "getNote(), index = $index")
        return notesRepository.get(index, context)
    }

    override fun updateData(context: Context) {
        info(TAG, "updateData(), get all records")
        val data = notesRepository.getAll(context)
        notes.value = data
    }

    fun getSearchResults(): LiveData<List<NoteModel>> {
        return searchResults
    }

    fun performSearch(context: Context, query: String, noteForSearch: NoteModel? = null) {
        Log.info(message = "performSearch()")
        val noteForSearchList: List<NoteModel> = if (noteForSearch != null) {
            listOf(noteForSearch)
        } else {
            notesRepository.getAll(context)
        }
        // Start a search
        viewModelScope.launch(defaultDispatcher + CoroutineName("NoteSearch")) {
            search(query, noteForSearchList, searchResults)
        }
    }

    companion object {
        private const val TAG = "NotesViewModel"
        // For coroutines
        val defaultDispatcher = Dispatchers.Default;
    }
}