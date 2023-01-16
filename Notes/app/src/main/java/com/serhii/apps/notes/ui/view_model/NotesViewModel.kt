/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.serhii.apps.notes.control.preferences.PreferenceManager.getNoteDisplayMode
import com.serhii.apps.notes.ui.data_model.NoteModel
import com.serhii.core.log.Log.Companion.info
import com.serhii.core.security.impl.crypto.CryptoError

/**
 * View model for managing UI data for User notes
 *
 * Responsibilities:
 * 1) Save data to Database
 * 2) Get latest data from Database
 * 3) Survive data during config changes
 * 3) Close Database when User is done with notes
 */
class NotesViewModel(application: Application) : AndroidViewModel(application), NotifyUpdateData {

    private val notes = MutableLiveData<List<NoteModel>>()
    private val errorState = MutableLiveData<CryptoError>()
    private val displayNoteMode = MutableLiveData<Int>()

    var cachedUserNotes: List<NoteModel>? = null
        private set

    private val notesRepository: NotesRepository

    init {
        notesRepository = NotesRepository(application, this)
        errorState.value = CryptoError.OK
        notes.value = ArrayList()
        val mode = getNoteDisplayMode(application.applicationContext)
        displayNoteMode.value = mode
        info(TAG, "NotesViewModel(), initialization is finished")
    }

    override fun onCleared() {
        super.onCleared()
        notesRepository.close()
        info(TAG, "onCleared(), clean up is finished")
    }

    fun getNotes(): LiveData<List<NoteModel>> {
        return notes
    }

    val errorStateData: LiveData<CryptoError>
        get() = errorState

    fun resetErrorState() { /* no-op */
    }

    fun cacheUserNote(userListCached: List<NoteModel>?) {
        cachedUserNotes = userListCached
    }

    fun onBackNavigation() {
        // Clear cached data. Not need it anymore.
        cachedUserNotes = null
    }

    val displayMode: LiveData<Int>
        get() = displayNoteMode

    fun setDisplayNoteMode(newMode: Int) {
        displayNoteMode.value = newMode
    }

    fun deleteNote(index: String?): Boolean {
        info(TAG, "deleteNote()")
        return notesRepository.delete(index!!)
    }

    fun addNote(noteModel: NoteModel?): Boolean {
        info(TAG, "addNote()")
        return notesRepository.add(noteModel!!)
    }

    fun updateNote(index: String, noteModel: NoteModel?): Boolean {
        info(TAG, "updateNote(), index = $index")
        return notesRepository.update(index, noteModel!!)
    }

    fun getNote(index: String): NoteModel? {
        info(TAG, "getNote(), index = $index")
        return notesRepository.get(index)
    }

    override fun updateData() {
        info(TAG, "retrieveData(), load data")
        val data = notesRepository.getAll()
        notes.value = data
    }

    companion object {
        private const val TAG = "NotesViewModel"
    }
}