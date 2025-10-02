package com.notes.notes_ui

import android.content.Context
import android.os.Parcelable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notes.notes_ui.screens.editor.getToolsList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

class NotesViewModel @Inject constructor(
    repository: Repository
) : ViewModel(), Callback {

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

    fun init(context: Context) {
        interaction.init(context)
    }

    override fun onCleared() {
        interaction.onClear()
    }

    fun onSelectAction(note: Notes) {
        _noteState.update { _notesState.value.first { note.id == it.id } }
    }

    fun onAddAction() {
        _noteState.update { Notes.NewNote() }
    }

    override fun onNewAdded(id: Long) = onSelectAction(Notes(id = id))

}