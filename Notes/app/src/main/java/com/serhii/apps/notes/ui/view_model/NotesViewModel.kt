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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.serhii.apps.notes.control.backup.BackupManager
import com.serhii.apps.notes.control.preferences.PreferenceManager.getNoteDisplayModePref
import com.serhii.apps.notes.database.UserNotesDatabase
import com.serhii.apps.notes.ui.data_model.NoteModel
import com.serhii.apps.notes.ui.repository.NotesRepository
import com.serhii.apps.notes.ui.repository.DataChangedListener
import com.serhii.apps.notes.ui.search.SearchableInfo
import com.serhii.core.log.Log.Companion.info
import com.serhii.core.security.impl.crypto.CryptoError

/**
 * View model for managing UI data for User notes
 *
 * Responsibilities:
 * 1) Save data to a database
 * 2) Get latest data from a database
 * 3) Survive data during config changes
 * 3) Clean up data when it si not needed
 */
class NotesViewModel(application: Application) : AndroidViewModel(application), DataChangedListener {

    private val notes = MutableLiveData<List<NoteModel>>()
    private val errorState = MutableLiveData<CryptoError>()
    private val displayNoteMode = MutableLiveData<Int>()
    fun getDisplayNoteMode(): LiveData<Int> = displayNoteMode

    var cachedUserNotes: List<NoteModel>? = null
        private set

    private val notesRepository: NotesRepository = NotesRepository(this)

    private val backupDataObserver: Observer<Boolean> =
        Observer<Boolean> { shouldUpdateData ->
            if (shouldUpdateData != null && shouldUpdateData) {
                info(TAG, "onChanged() $shouldUpdateData IN")
                updateData(getApplication())
                BackupManager.dataUpdated()
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
        // Clear cached data. Not need it anymore.
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
        info(TAG, "retrieveData(), load data")
        val data = notesRepository.getAll(context)
        notes.value = data
    }

    // TODO: Simplify this
    fun performSearch(context: Context, query: String, noteForSearch: NoteModel? = null) {

        // Remove extra spaces
        val searchedText = query.trim()

        val noteList: List<NoteModel> = if (noteForSearch != null) {
            listOf(noteForSearch)
        } else {
            notesRepository.getAll(context)
        }

        val newList = noteList.filter { element ->
            val noteText = element.note
            val listNote = element.listNote
            val noteTitle = element.title

            val rangeItemTitle = searchAllOccurrences(noteTitle, searchedText)
            val rangeItemNoteText = searchAllOccurrences(noteText, searchedText)

            element.queryInfo = SearchableInfo(rangeItemTitle, rangeItemNoteText)

            val newListWithMatchedText = listNote.filter { listElement ->
                listElement.note.contains(searchedText)
            }

            // Check if any item has text which we search
            rangeItemTitle.isNotEmpty() || rangeItemNoteText.isNotEmpty() || newListWithMatchedText.isNotEmpty()
        }

        info(TAG, "performSearch() got result sz = ${newList.size}")

        notes.value = newList
    }

    // TODO: Simplify this
    private fun searchAllOccurrences(text: String, search: String): List<IntRange> {

        var start = 0
        var index = 0

        val listRanges = mutableListOf<IntRange>()

        while (true) {
            index = text.indexOf(search, start, true)

            if (index == -1)
                break

            listRanges.add(index..(search.length + index))

            if ((index + search.length - 1) >= text.length) {
                break
            }

            start = index + search.length
        }

        return listRanges
    }

    /**
     * ViewModel factory for [com.serhii.apps.notes.ui.view_model.NotesViewModel] class
     * TODO: Actually this doesn't do anything usefully, maybe remove ?
     */
    class NotesViewModelFactory(application: Application) : ViewModelProvider.AndroidViewModelFactory(application) {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            info(TAG, "create(), IN")
            val viewModel = super.create(modelClass)
            info(TAG, "create(), created: $viewModel")
            return viewModel
        }

        companion object {
            private const val TAG = "NotesViewModelFactory"
        }
    }

    companion object {
        private const val TAG = "NotesViewModel"
    }
}