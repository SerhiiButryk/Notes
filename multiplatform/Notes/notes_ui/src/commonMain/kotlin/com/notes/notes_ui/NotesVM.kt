package com.notes.notes_ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.data.Notes
import com.notes.notes_ui.data.getToolsList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.Lazily
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class NotesVM(
    // For test support
    scopeOverride: CoroutineScope? = null
) : ViewModel() {

    private val scope: CoroutineScope = scopeOverride ?: viewModelScope

    private val repository = object : Repository {

        override fun getNotes(): Flow<List<Notes>> {
            return flow {
                emit(listOf(Notes("content1", id = 1), Notes("content2", id = 2)))
            }
        }

        override fun getNotes(id: Long): Flow<Notes?> {
            return flow {
                emit(Notes("content1", id = 1))
            }
        }

        override fun saveNote(note: Notes, onNewAdded: suspend (Long) -> Unit) {

        }

        override fun deleteNote(note: Notes, callback: (Long) -> Unit) {

        }

        override fun clear() {

        }
    }

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