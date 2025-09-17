package com.notes.notes_ui

import com.mohamedrejeb.richeditor.model.RichTextState
import com.notes.notes_ui.screens.editor.EditorCommand

class NotesInteraction {

    fun sendEditorCommand(command: EditorCommand) {
        command.execCommand()
    }

    fun saveContent(state: RichTextState) {

    }

}