package com.notes.notes_ui.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextRange
import api.data.Notes
import dev.mkeeda.arranger.richtext.RichString
import dev.mkeeda.arranger.richtext.editor.RichTextState

fun EditorState(richString: Any?): RichTextState {
    val state = RichTextState(
        initialText = richString as? RichString ?: RichString(""),
        initialSelection = TextRange.Zero,
    )
    return state
}

@Composable
fun rememberRichEditorState(notes: Notes) =
    remember(notes.content, notes.richState) {
        if (notes.richState is RichTextState) {
            mutableStateOf(notes.richState as RichTextState)
        } else {
            mutableStateOf(EditorState(notes.richState))
        }
    }