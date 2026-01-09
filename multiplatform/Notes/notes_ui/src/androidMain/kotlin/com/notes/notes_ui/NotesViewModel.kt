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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(
    // TODO: This may be simplified
    appRepository: Repository = AppRepository(
        RemoteRepository(provideDataStoreService())
    ),
    // For test support
    scopeOverride: CoroutineScope? = null
) : ViewModel(), Callback {

    private val scope: CoroutineScope

    private val interaction: Interactor = Interactor(appRepository, this)

    // A state to hold all the notes
    val notesState: StateFlow<List<Notes>>

    // A state to hold the note which is open in Editor
    private val _noteState = MutableStateFlow(Notes.AbsentNote())
    val noteState = _noteState.asStateFlow()

    val richTools = getToolsList(interaction)

    init {

        if (scopeOverride != null) {
            scope = scopeOverride
        } else {
            scope = viewModelScope
        }

        notesState = interaction
            .getNotes()
            .stateIn(
                scope = scope,
                started = WhileSubscribed(stopTimeoutMillis = 5000),
                emptyList(),
            )
    }

    fun init(context: Context) {
        interaction.init(context)
    }

    override fun onCleared() {
        interaction.onClear()
    }

    suspend fun onSelectAction(note: Notes) {
        val found = notesState.value.firstOrNull { note.id == it.id }
        if (found == null) {
            _noteState.emit(interaction.getNotes(note.id).first()!!)
        } else {
            _noteState.emit(found)
        }
        interaction.onNoteOpened()
    }

    suspend fun onAddAction() {
        _noteState.emit(Notes.NewNote())
        interaction.onNoteOpened()
    }

    override fun onAdded(id: Long) {
        scope.launch {
            onSelectAction(Notes(id = id))
        }
    }

    override fun onDeleted(id: Long) {
        scope.launch {
            _noteState.emit(Notes.DeletedNote())
        }
    }

    suspend fun onNavigatedBack() {
        _noteState.emit(Notes.AbsentNote())
    }

    fun sendEditorCommand(command: EditorCommand) {
        interaction.sendEditorCommand(command)
    }
}
