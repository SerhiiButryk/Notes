package com.notes.notes_ui

import api.Platform
import api.data.Attachments
import api.data.Notes
import api.data.UserFile
import api.repo.RepoCallback
import api.repo.Repository
import com.notes.notes_ui.editor.Command
import com.notes.notes_ui.editor.HtmlParser
import com.notes.notes_ui.editor.RichEditor
import com.notes.notes_ui.editor.toHtml
import dev.mkeeda.arranger.richtext.editor.RichTextState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class Interactor(
    private val repository: Repository,
    private val repoCallback: RepoCallback,
    private val textEditor: RichEditor = RichEditor(),
    private val scope: CoroutineScope,
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

    fun saveNote(
        state: RichTextState,
        note: Notes,
    ) {
        // Transforms rich state to HTML and saves it
        scope.launch(Dispatchers.Default) {
            repository.saveNote(note.copy(content = state.toHtml())) {
                repoCallback.onNoteAdded(it)
            }
        }
    }

    fun deleteNote(note: Notes) {
        repository.deleteNote(note) {
            repoCallback.onEditorNavBack()
        }
    }

    // Transforms HTML to rich state and sets 'richString' field
    fun getNotes(): Flow<List<Notes>> =
        flow {
            repository
                .getNotes()
                .collect { list ->
                    val newList = mutableListOf<Notes>()
                    list.forEach { note ->
                        Platform().logger.logi("getNotes(): Parsing note ('${note.id}')...")
                        val richString = HtmlParser().parse(note.content)
                        note.richString = richString
                        newList.add(note)
                    }
                    emit(newList)
                }
        }.flowOn(Dispatchers.Default)

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

    suspend fun onDeleteAttachment(file: UserFile): Boolean = repository.onDeleteAttachment(file)
}
