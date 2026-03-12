package com.notes.notes_ui.editor

import api.data.Notes
import dev.mkeeda.arranger.richtext.RichString
import dev.mkeeda.arranger.richtext.editor.RichTextState

fun createEditorState(note: Notes): RichTextState =
    RichTextState(
        initialText = note.richString as? RichString ?: RichString(""),
    )
