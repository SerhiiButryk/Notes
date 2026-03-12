package com.notes.notes_ui.editor

import dev.mkeeda.arranger.richtext.BlockTypeAttributeKey
import dev.mkeeda.arranger.richtext.HeadingKey
import dev.mkeeda.arranger.richtext.HeadingLevel
import dev.mkeeda.arranger.richtext.ListIndentLevel
import dev.mkeeda.arranger.richtext.SpanAttributeKey
import dev.mkeeda.arranger.richtext.TextAlignment
import dev.mkeeda.arranger.richtext.TextAlignmentKey
import dev.mkeeda.arranger.richtext.editor.RichTextState
import dev.mkeeda.arranger.richtext.editor.applyFormat
import dev.mkeeda.arranger.richtext.editor.clearFormats
import dev.mkeeda.arranger.richtext.editor.removeFormat
import dev.mkeeda.arranger.richtext.editor.toggleFormat

class RichEditor {

    fun onCommand(
        command: Command,
        state: RichTextState,
    ) {
        when (command) {
            is Command.Undo -> {
                if (state.undoState.canUndo) {
                    state.undoState.undo()
                }
            }

            is Command.ClearFormatting -> {
                state.clearFormats()
            }

            is Command.ClearText -> {
                state.edit { replace(0 until textLength, "") }
            }

            is Command.HFormat -> {
                val level = command.level
                if (state.currentAttributes[HeadingKey] == level) {
                    state.removeFormat(HeadingKey)
                } else {
                    state.applyFormat(HeadingKey, level)
                }
            }

            is Command.Redo -> {
                if (state.undoState.canRedo) {
                    state.undoState.redo()
                }
            }

            is Command.StringFormat -> {
                val key = command.attribute
                state.toggleFormat(key)
            }

            is Command.TextAlign -> {
                val level = command.alignment
                if (state.currentAttributes[TextAlignmentKey] == level) {
                    state.removeFormat(TextAlignmentKey)
                } else {
                    state.applyFormat(TextAlignmentKey, level)
                }
            }

            is Command.List -> {
                val type = command.type
                val level = command.level
                if (state.currentAttributes.containsKey(type)) {
                    state.removeFormat(type)
                } else {
                    state.applyFormat(type, level)
                }
            }
        }
    }
}

sealed class Command {
    class Undo : Command()

    class Redo : Command()

    class ClearText : Command()

    class ClearFormatting : Command()

    class HFormat(
        val level: HeadingLevel,
    ) : Command()

    class StringFormat(
        val attribute: SpanAttributeKey<Unit>,
    ) : Command()

    class TextAlign(
        val alignment: TextAlignment,
    ) : Command()

    class List(
        val type: BlockTypeAttributeKey<ListIndentLevel>,
        val level: ListIndentLevel,
    ) : Command()
}
