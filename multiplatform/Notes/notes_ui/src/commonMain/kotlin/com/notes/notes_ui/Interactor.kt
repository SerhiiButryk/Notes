package com.notes.notes_ui

import api.data.Notes
import com.notes.notes_ui.features.RedoUndoAction
import kotlinx.coroutines.flow.Flow

interface EditorState {
    fun getHtml(): String
}

interface EditorCommand {
    fun execCommand()
    fun undo()
    fun isTextInputCommand(): Boolean = false
}

interface RepoCallback {
    fun onNoteAdded(id: Long)

    fun onNoteDeleted(id: Long)
    fun onNoteNavigateBack()
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

    fun init(context: Any)

    fun clear()
}

class Interactor(
    private val repository: Repository,
    private val repoCallback: RepoCallback,
) {
    // Redo, undo edit command support. Called from the UI menu.
    val redoUndoAction = RedoUndoAction()

    fun onNoteOpened() {
        redoUndoAction.clearStates()
    }

    fun onNoteNavigateBack() {
        repoCallback.onNoteNavigateBack()
    }

    // User changes text
    fun sendEditorCommand(command: EditorCommand) {
        redoUndoAction.onEditAction(command)
        // It has already been applied
        if (command.isTextInputCommand()) {
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
            repoCallback.onNoteAdded(it)
        }
    }

    fun deleteNote(note: Notes) {
        repository.deleteNote(note) {
            repoCallback.onNoteDeleted(it)
        }
    }

    fun getNotes(): Flow<List<Notes>> = repository.getNotes()

    fun getNotes(id: Long): Flow<Notes?> = repository.getNotes(id)

    fun init(context: Any) {
        repository.init(context)
    }

    fun onClear() {
        repository.clear()
    }
}
