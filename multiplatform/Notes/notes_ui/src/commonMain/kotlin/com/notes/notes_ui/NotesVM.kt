package com.notes.notes_ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.Platform
import api.data.Notes
import api.data.NotesCollection
import api.repo.RepoCallback
import api.repo.Repository
import com.notes.notes_ui.data.getToolsList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.Lazily
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class NotesVM(
    repository: Repository = Platform().appRepo,
    // For test support
    scopeOverride: CoroutineScope? = null,
) : ViewModel() {

    private val scope: CoroutineScope = scopeOverride ?: viewModelScope

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
    val notesState: StateFlow<NotesCollection> = interactor
        .getNotes()
        .map { NotesCollection(it) }
        .stateIn(
            scope = scope,
            // For Desktop app it should be fine
            started = Lazily,
            NotesCollection(emptyList()),
        )

    // A state to hold the note which is open in Editor
    private val _noteState = MutableStateFlow(Notes.AbsentNote())
    val noteState = _noteState.asStateFlow()

    override fun onCleared() {
        interactor.onClear()
    }
}