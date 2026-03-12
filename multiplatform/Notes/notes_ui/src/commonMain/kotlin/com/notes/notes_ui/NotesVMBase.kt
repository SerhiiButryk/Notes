package com.notes.notes_ui

import api.data.Notes
import api.data.NotesCollection
import api.repo.RepoCallback
import api.repo.Repository
import com.notes.notes_ui.components.ViewModelCommand
import com.notes.notes_ui.models.getToolsList
import com.notes.ui.model.BaseAppVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

open class NotesVMBase(
    scopeOverride: CoroutineScope?,
    appRepository: Repository
) : BaseAppVM(scopeOverride), RepoCallback {

    protected val interactor =
        Interactor(repository = appRepository, repoCallback = this, scope = scope)

    // A state to hold all the notes in the UI list
    val notesState: StateFlow<NotesCollection> =
        interactor
            .getNotes()
            .map { NotesCollection(it) }
            .stateIn(
                scope = scope,
                started = WhileSubscribed(stopTimeoutMillis = 5000),
                NotesCollection(),
            )

    // A state to hold the note which is open in UI Editor
    protected val _noteState = MutableStateFlow(Notes.AbsentNote())
    val noteState = _noteState.asStateFlow()

    // UI Editor rich tools
    val richTools = getToolsList(interactor)

    // To ask UI about something
    protected val viewModelCommand = Channel<ViewModelCommand>(capacity = Channel.BUFFERED)
    val vmCommands = viewModelCommand.receiveAsFlow()

    override fun onCleared() {
        super.onCleared()
        interactor.onClear()
    }

    // User selected a note from list ui
    suspend fun onSelectAction(note: Notes) {
        val found = notesState.value.collection.firstOrNull { note.id == it.id }
        if (found == null) {
            val note = interactor.getNotes(note.id).first()!!
            _noteState.emit(note)
        } else {
            _noteState.emit(found)
        }
        interactor.onEditorOpen()
    }

    // User clicked on '+' button in ui to create an empty note
    suspend fun onAddAction() {
        _noteState.emit(Notes.NewNote())
        interactor.onEditorOpen()
    }

    // Note has been updated in repository
    override fun onNoteAdded(id: Long) {
        scope.launch {
            // Select updated note to make sure that ui has the latest state
            onSelectAction(Notes(id = id))
        }
    }

    // Need to nav back because of some state changes or user interaction
    override fun onEditorNavBack() {
        scope.launch {
            onNavigatedBack()
        }
    }

    suspend fun onNavigatedBack() {
        // Emitting 'AbsentNote' to show 'Select an item' message in
        // the editor to inform user that editor is inactive
        // he/she should select a note from list ui
        _noteState.emit(Notes.AbsentNote())
    }

}