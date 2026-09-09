package com.notes.notes_ui

import api.data.Attachments
import api.data.Notes
import api.data.UserFile
import api.repo.RepoCallback
import api.repo.Repository
import com.notes.notes_ui.editor.Command
import com.notes.notes_ui.editor.RichEditor
import com.notes.notes_ui.editor.mapToHtml
import dev.mkeeda.arranger.richtext.editor.RichTextState
import dev.mkeeda.arranger.richtext.html.toHtml
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class Interactor(
    private val repository: Repository,
    private val repoCallback: RepoCallback,
    private val textEditor: RichEditor = RichEditor(),
    private val scope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    fun onEditorOpen() {
    }

    fun onEditorNavBack() {
        repoCallback.onEditorNavBack()
    }

    fun sendEditorCommand(
        command: Command,
        state: RichTextState,
    ) {
        textEditor.onCommand(command, state)
    }

    fun isActiveCommand(
        command: Command,
        state: RichTextState,
    ): Boolean = textEditor.isActive(command, state)

    fun saveNote(
        state: RichTextState,
        note: Notes,
    ) {
        scope.launch(context = dispatcher) {
            repository.saveNote(note.copy(content = state.richString.toHtml())) {
                repoCallback.onNoteAdded(it)
            }
        }
    }

    fun deleteNote(note: Notes) {
        repository.deleteNote(note) {
            repoCallback.onEditorNavBack()
        }
    }

    fun getNotes(): Flow<List<Notes>> =
        flow {
            repository
                .getNotes()
                .mapToHtml(context = dispatcher)
                .collect { emit(it) }
        }

    fun getNotes(id: Long): Flow<Notes?> = repository.getNotes(id)

    fun onClear() {
        repository.clear()
    }

    suspend fun onAttachments(
        file: Any,
        noteId: Long,
        info: Any?,
    ): Boolean = repository.onAttachments(file, noteId, info)

    fun getAttachments(): Flow<Attachments> = repository.getAttachments()

    suspend fun onDeleteAttachment(file: UserFile): Boolean =
        repository.onDeleteAttachment(file)
}
