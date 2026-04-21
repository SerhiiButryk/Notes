package com.notes.notes_ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.Platform
import api.data.Notes
import api.repo.RepoCallback
import com.notes.notes_ui.data.getToolsList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.Lazily
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

class NotesVM(
    // For test support
    scopeOverride: CoroutineScope? = null
) : ViewModel() {

    private val scope: CoroutineScope = scopeOverride ?: viewModelScope

    private val repository = Platform().appRepo

    private val interactor = Interactor(repository, object : RepoCallback {
        override fun onNoteAdded(id: Long) {
        }

        override fun onNoteDeleted(id: Long) {
        }

        override fun onNoteNavigateBack() {
        }
    })


    val richTools = getToolsList(interactor)

    // A state to hold all the notes
    val notesState: StateFlow<List<Notes>> = interactor
        .getNotes()
        .stateIn(
            scope = scope,
            // For Desktop app it should be fine
            started = Lazily,
            emptyList(),
        )

    // A state to hold the note which is open in Editor
    private val _noteState = MutableStateFlow(Notes.AbsentNote())
    val noteState = _noteState.asStateFlow()

    override fun onCleared() {
        interactor.onClear()
    }
}