package com.notes.notes_ui

import android.content.Context
import android.os.Parcelable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notes.notes_ui.screens.editor.getToolsList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    repository: Repository
) : ViewModel(), Callback {

    // Class to model different action for note data
    // This annotation could be redundant as
    // the class is already stable, because all properties are stable.
    // However, keep it for clarity.
    @Stable
    @Parcelize
    data class Notes(
        val content: String = "",
        val id: Long = 0,
        val userId: String = "",
        val time: String = ""
    ) :
        Parcelable {

        companion object {
            fun NewNote() = Notes(id = -1)
            fun AbsentNote() = Notes(id = -2)
        }
    }

    // Class to model different action for note UI
    @Stable
    sealed class Actions {
        class NavBackAction : Actions()
    }

    private val interaction: Interaction = Interaction(repository, this)

    // A state to hold all the notes
    private val _notesState = interaction.getNotes()
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(stopTimeoutMillis = 5000),
            emptyList()
        )

    val notesState = _notesState

    // A state to hold the note which is open in Editor
    private val _noteState = MutableStateFlow(Notes.AbsentNote())
    val noteState = _noteState.asStateFlow()

    val richTools = getToolsList(interaction)

    private val actionsUI = MutableSharedFlow<Actions>()

    fun getActions() = actionsUI
        .shareIn(
            scope = viewModelScope,
            started = WhileSubscribed()
        )

    fun init(context: Context) {
        interaction.init(context)
    }

    override fun onCleared() {
        interaction.onClear()
    }

    fun onSelectAction(note: Notes) {
        val found = _notesState.value.firstOrNull { note.id == it.id }
        if (found == null) {
            viewModelScope.launch {
                _noteState.update { interaction.getNotes(note.id).first()!! }
            }
        } else {
            _noteState.update { found }
        }
    }

    fun onAddAction() {
        _noteState.update { Notes.NewNote() }
    }

    override fun onAdded(id: Long) = onSelectAction(Notes(id = id))

    override fun onDeleted(id: Long) {
        _noteState.update { Notes.AbsentNote() }
        // Close Editor UI screen if it's open
        viewModelScope.launch {
            actionsUI.emit(Actions.NavBackAction())
        }
    }

}