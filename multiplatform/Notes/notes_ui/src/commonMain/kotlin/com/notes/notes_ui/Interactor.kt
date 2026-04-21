package com.notes.notes_ui

import api.data.Notes
import api.repo.RepoCallback
import api.repo.Repository
import com.notes.notes_ui.editor.EditorCommand
import com.notes.notes_ui.editor.EditorState
import com.notes.notes_ui.features.RedoUndoAction
import kotlinx.coroutines.flow.Flow

class Interactor(
    private val repository: Repository,
    private val repoCallback: RepoCallback,
) {
    // Redo, undo edit command support. Called from the UI menu.
    val redoUndoAction = RedoUndoAction()

    fun onNoteOpened() {
        redoUndoAction.clear()
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

    fun onClear() {
        repository.clear()
    }
}
