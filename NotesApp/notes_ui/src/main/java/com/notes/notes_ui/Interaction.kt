package com.notes.notes_ui

import android.content.Context
import com.notes.notes_ui.NotesViewModel.Notes
import kotlinx.coroutines.flow.Flow

interface EditorState {
    fun getHtml(): String
}

interface EditorCommand {
    fun execCommand()
}

interface Callback {
    fun onAdded(id: Long)
    fun onDeleted(id: Long)
}

interface Repository {
    fun getNotes(): Flow<List<Notes>>
    fun getNotes(id: Long): Flow<Notes?>
    fun saveNote(note: Notes, onNewAdded: suspend (Long) -> Unit)
    fun deleteNote(note: Notes, callback: (Long) -> Unit)
    fun init(context: Context)
    fun clear()
}

class Interaction(private val repository: Repository, private val callback: Callback) {

    fun sendEditorCommand(command: EditorCommand) {
        command.execCommand()
    }

    fun saveContent(state: EditorState, note: Notes) {
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

    fun getNotes(): Flow<List<Notes>> {
        return repository.getNotes()
    }

    fun getNotes(id: Long): Flow<Notes?> {
        return repository.getNotes(id)
    }

    fun init(context: Context) {
        repository.init(context)
    }

    fun onClear() {
        repository.clear()
    }

}