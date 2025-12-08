package com.notes.notes_ui.screens.editor

import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import com.mohamedrejeb.richeditor.model.RichTextState
import com.notes.notes_ui.EditorCommand

open class UndoableCommand(protected val richTextState: RichTextState) : EditorCommand {

    private var savedState: RichTextState? = null

    override fun execCommand() {
        savedState = richTextState.copy()
    }

    override fun undo() {
        richTextState.restoreState(savedState!!)
    }
}

class EditCommand(val spanStyle: SpanStyle, richTextState: RichTextState) :
    UndoableCommand(richTextState) {

    override fun execCommand() {
        super.execCommand()
        // Simulates toggle behavior. Will adding or removing style as needed
        richTextState.toggleSpanStyle(spanStyle)
    }
}

class ClearCommand(richTextState: RichTextState) : UndoableCommand(richTextState) {
    override fun execCommand() {
        super.execCommand()
        richTextState.clear()
    }
}

class AlignCommand(richTextState: RichTextState, val paragraphStyle: ParagraphStyle) :
    UndoableCommand(richTextState) {
    override fun execCommand() {
        super.execCommand()
        // Simulates toggle behavior. Will adding or removing style as needed
        richTextState.toggleParagraphStyle(paragraphStyle)
    }
}

class OrderedListCommand(richTextState: RichTextState) : UndoableCommand(richTextState) {
    override fun execCommand() {
        super.execCommand()
        richTextState.toggleOrderedList()
    }
}

class UnorderedListCommand(richTextState: RichTextState) : UndoableCommand(richTextState) {
    override fun execCommand() {
        richTextState.toggleUnorderedList()
    }
}

class TextInputCommand(
    val new: String,
    val old: String,
    val state: RichTextState
) : EditorCommand {

    override fun execCommand() {
        state.setHtml(new)
    }

    override fun undo() {
        state.setHtml(old)
    }
}