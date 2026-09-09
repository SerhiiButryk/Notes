package com.notes.notes_ui.editor

import dev.mkeeda.arranger.richtext.BackgroundColorKey
import dev.mkeeda.arranger.richtext.BlockTypeAttributeKey
import dev.mkeeda.arranger.richtext.HeadingKey
import dev.mkeeda.arranger.richtext.HeadingLevel
import dev.mkeeda.arranger.richtext.ListIndentLevel
import dev.mkeeda.arranger.richtext.RgbaColor
import dev.mkeeda.arranger.richtext.SpanAttributeKey
import dev.mkeeda.arranger.richtext.TextAlignment
import dev.mkeeda.arranger.richtext.TextAlignmentKey
import dev.mkeeda.arranger.richtext.TextColorKey
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

            is Command.ColorFill -> {
                if (command.color1 != null) {
                    if (RgbaColor(0xFF000000) == command.color1) {
                        state.removeFormat(TextColorKey)
                    } else if (state.currentAttributes[TextColorKey] != command.color1) {
                        state.applyFormat(TextColorKey, command.color1)
                    }
                }
                if (command.color2 != null) {
                    if (RgbaColor(0xFFFFFFFF) == command.color2) {
                        state.removeFormat(BackgroundColorKey)
                    } else if (state.currentAttributes[BackgroundColorKey] != command.color2) {
                        state.applyFormat(BackgroundColorKey, command.color2)
                    }
                }
            }

        }
    }

    fun isActive(
        command: Command,
        state: RichTextState,
    ): Boolean {
        return when (command) {

            is Command.Undo -> {
                state.undoState.canUndo
            }

            is Command.ClearFormatting, is Command.ClearText -> {
                false
            }

            is Command.HFormat -> {
                val level = command.level
                state.currentAttributes[HeadingKey] == level
            }

            is Command.Redo -> {
                state.undoState.canRedo
            }

            is Command.StringFormat -> {
                val key = command.attribute
                state.currentAttributes.containsKey(key)
            }

            is Command.TextAlign -> {
                val level = command.alignment
                state.currentAttributes[TextAlignmentKey] == level
            }

            is Command.List -> {
                val type = command.type
                state.currentAttributes.containsKey(type)
            }

            is Command.ColorFill -> {
                state.currentAttributes[BackgroundColorKey] != null ||
                        state.currentAttributes[TextColorKey] != null
            }
        }
    }
}

fun getBackgroundTextColor(state: RichTextState): RgbaColor {
    val backgroundColorValue = state.currentAttributes[BackgroundColorKey]
    return backgroundColorValue ?: RgbaColor(0xFFFFFFFF)
}

fun getTextColor(state: RichTextState): RgbaColor {
    val textColorValue = state.currentAttributes[TextColorKey]
    return textColorValue ?: RgbaColor(0xFF000000)
}

sealed class Command {
    class Undo : Command()

    class Redo : Command()

    class ClearText : Command()

    class ClearFormatting : Command()

    class ColorFill(val color1: RgbaColor?, val color2: RgbaColor?) : Command()

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
