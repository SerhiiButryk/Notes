package com.notes.notes_ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notes.api.data.Notes
import com.notes.notes_ui.data.AppRepository
import com.notes.notes_ui.screens.editor.getToolsList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(
    appRepository: Repository = AppRepository(),
) : ViewModel(),
    Callback {
    private val interaction: Interaction = Interaction(appRepository, this)

    // A state to hold all the notes
    private val _notesState =
        interaction
            .getNotes()
            .stateIn(
                scope = viewModelScope,
                started = WhileSubscribed(stopTimeoutMillis = 5000),
                emptyList(),
            )

    val notesState = _notesState

    // A state to hold the note which is open in Editor
    private val _noteState = MutableStateFlow(Notes.AbsentNote())
    val noteState = _noteState.asStateFlow()

    val richTools = getToolsList(interaction)

    fun init(context: Context) {
        interaction.init(context)
    }

    override fun onCleared() {
        interaction.onClear()
    }

    suspend fun onSelectAction(note: Notes) {
        val found = _notesState.value.firstOrNull { note.id == it.id }
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
        viewModelScope.launch {
            onSelectAction(Notes(id = id))
        }
    }

    override fun onDeleted(id: Long) {
        viewModelScope.launch {
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
