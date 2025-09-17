package com.notes.notes_ui.screens.editor

import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import com.mohamedrejeb.richeditor.model.RichTextState

interface EditorCommand {
    fun execCommand()
}

class EditCommand(val spanStyle: SpanStyle, val richTextState: RichTextState) : EditorCommand {
    override fun execCommand() {
        // Simulates toggle behavior. Will adding or removing style as needed
        richTextState.toggleSpanStyle(spanStyle)
    }
}

class ClearCommand(val richTextState: RichTextState) : EditorCommand {
    override fun execCommand() {
        richTextState.clear()
    }
}

class AlignCommand(val richTextState: RichTextState, val paragraphStyle: ParagraphStyle) : EditorCommand {
    override fun execCommand() {
        // Simulates toggle behavior. Will adding or removing style as needed
        richTextState.toggleParagraphStyle(paragraphStyle)
    }
}

class OrderedListCommand(val richTextState: RichTextState) : EditorCommand {
    override fun execCommand() {
        richTextState.toggleOrderedList()
    }
}

class UnorderedListCommand(val richTextState: RichTextState) : EditorCommand {
    override fun execCommand() {
        richTextState.toggleUnorderedList()
    }
}