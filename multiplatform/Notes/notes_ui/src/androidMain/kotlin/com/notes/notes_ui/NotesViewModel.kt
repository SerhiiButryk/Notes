package com.notes.notes_ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.data.Notes
import api.provideDataStoreService
import com.notes.notes_ui.data.AppRepository
import com.notes.notes_ui.data.RemoteRepository
import com.notes.notes_ui.screens.editor.getToolsList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(
    // TODO: This may be simplified
    appRepository: Repository = AppRepository(
        RemoteRepository(provideDataStoreService())
    ),
    // For test support
    scopeOverride: CoroutineScope? = null
) : ViewModel(), RepoCallback {

    private val scope: CoroutineScope = scopeOverride ?: viewModelScope

    private val interaction: Interactor = Interactor(appRepository, this)

    // A state to hold all the notes
    val notesState: StateFlow<List<Notes>> = interaction
        .getNotes()
        .stateIn(
            scope = scope,
            started = WhileSubscribed(stopTimeoutMillis = 5000),
            emptyList(),
        )

    // A state to hold the note which is open in Editor
    private val _noteState = MutableStateFlow(Notes.AbsentNote())
    val noteState = _noteState.asStateFlow()

    val richTools = getToolsList(interaction)

    private val uiEvents = Channel<UiEvent>(capacity = Channel.BUFFERED)
    val events = uiEvents.receiveAsFlow()

    sealed class UiEvent {
        class NavigateToListPane : UiEvent()
    }

    fun init(context: Context) {
        interaction.init(context)
    }

    override fun onCleared() {
        interaction.onClear()
    }

    // User selected a note from list ui
    suspend fun onSelectAction(note: Notes) {
        val found = notesState.value.firstOrNull { note.id == it.id }
        if (found == null) {
            val note = interaction.getNotes(note.id).first()!!
            _noteState.emit(note)
        } else {
            _noteState.emit(found)
        }
        interaction.onNoteOpened()
    }

    // User clicked on '+' button in ui to create an empty note
    suspend fun onAddAction() {
        _noteState.emit(Notes.NewNote())
        interaction.onNoteOpened()
    }

    override fun onNoteAdded(id: Long) { // Note has been updated in repository
        scope.launch {
            // Select updated note to make sure that ui has the latest state
            onSelectAction(Notes(id = id))
        }
    }

    override fun onNoteDeleted(id: Long) { // Note has been deleted in repository
        scope.launch {
            uiEvents.send(UiEvent.NavigateToListPane())
        }
    }

    override fun onNoteNavigateBack() {
        scope.launch {
            uiEvents.send(UiEvent.NavigateToListPane())
        }
    }

    suspend fun onNavigatedBack() {
        // Emitting 'AbsentNote' to show 'Select an item' message in
        // the editor to inform user that editor is inactive
        // he/she should select a note from list ui
        _noteState.emit(Notes.AbsentNote())
    }

    fun sendEditorCommand(command: EditorCommand) {
        interaction.sendEditorCommand(command)
    }
}
