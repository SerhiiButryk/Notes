package com.notes.notes_ui

import android.os.Parcelable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notes.notes_ui.data.getNoteList
import com.notes.notes_ui.screens.editor.getToolsList
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.parcelize.Parcelize

private var uuid = 1L

class NotesViewModel : ViewModel() {

    // This annotation could be redundant as
    // the class is already stable, because all properties are stable.
    // However, keep it for clarity.
    @Stable
    @Parcelize
    data class Notes(
        val content: String = "",
        val id: String = (uuid++).toString()
    ) :
        Parcelable {

        companion object {
            fun EmptyNote() = Notes("", "")
        }
    }

    private val interaction = NotesInteraction()

    private val _notesState = getNoteList()
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(stopTimeoutMillis = 5000),
            emptyList()
        )

    val notesState = _notesState

    val richTools = getToolsList(interaction)

}