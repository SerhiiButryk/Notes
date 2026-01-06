package com.notes.notes_ui

import android.content.Context
import com.notes.api.data.Notes
import com.notes.notes_ui.feature.RedoUndoAction
import com.notes.notes_ui.screens.editor.TextInputCommand
import kotlinx.coroutines.flow.Flow

interface EditorState {
    fun getHtml(): String
}

interface EditorCommand {
    fun execCommand()

    fun undo()
}

interface Callback {
    fun onAdded(id: Long)

    fun onDeleted(id: Long)
}

interface Repository {
    fun getNotes(): Flow<List<Notes>>

    fun getNotes(id: Long): Flow<Notes?>

    fun saveNote(
        note: Notes,
        onNewAdded: suspend (Long) -> Unit,
    )

    fun deleteNote(
        note: Notes,
        callback: (Long) -> Unit,
    )

    fun init(context: Context)

    fun clear()
}

class Interaction(
    private val repository: Repository,
    private val callback: Callback,
) {
    // Redo, undo edit command support. Called from the menu.
    val redoUndoAction = RedoUndoAction()

    fun onNoteOpened() {
        redoUndoAction.clearStates()
    }

    // User changes text
    fun sendEditorCommand(command: EditorCommand) {
        redoUndoAction.onEditAction(command)
        // It has already been applied
        if (command is TextInputCommand) {
            return
        }
        command.execCommand()
    }

    fun saveContent(
        state: EditorState,
        note: Notes,
    ) {
        val html = state.getHtml()
        repository.saveNote(note.copy(content = html)) {
            callback.onAdded(it)
        }
    }

    fun deleteNote(note: Notes) {
        repository.deleteNote(note) {
            callback.onDeleted(it)
        }
    }

    fun getNotes(): Flow<List<Notes>> = repository.getNotes()

    fun getNotes(id: Long): Flow<Notes?> = repository.getNotes(id)

    fun init(context: Context) {
        repository.init(context)
    }

    fun onClear() {
        repository.clear()
    }
}
