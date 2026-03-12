package com.notes.notes_ui.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.FormatAlignLeft
import androidx.compose.material.icons.automirrored.outlined.FormatAlignRight
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.FormatAlignCenter
import androidx.compose.material.icons.outlined.FormatBold
import androidx.compose.material.icons.outlined.FormatItalic
import androidx.compose.material.icons.outlined.FormatUnderlined
import androidx.compose.material.icons.outlined.StrikethroughS
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import api.data.AppSettings
import api.data.Notes
import com.notes.notes_ui.Interactor
import com.notes.notes_ui.editor.Command
import com.notes.ui.Format_list_numbered
import com.notes.ui.List
import com.notes.ui.Redo
import com.notes.ui.SAVE_ICON
import com.notes.ui.Undo
import com.notes.ui.h1FormatIcon
import com.notes.ui.h2FormatIcon
import com.notes.ui.h3FormatIcon
import com.notes.ui.h4FormatIcon
import com.notes.ui.h5FormatIcon
import com.notes.ui.h6FormatIcon
import com.notes.ui.toPainter
import dev.mkeeda.arranger.richtext.BoldKey
import dev.mkeeda.arranger.richtext.BulletListKey
import dev.mkeeda.arranger.richtext.HeadingLevel
import dev.mkeeda.arranger.richtext.ItalicKey
import dev.mkeeda.arranger.richtext.ListIndentLevel
import dev.mkeeda.arranger.richtext.OrderedListKey
import dev.mkeeda.arranger.richtext.StrikethroughKey
import dev.mkeeda.arranger.richtext.TextAlignment
import dev.mkeeda.arranger.richtext.UnderlineKey
import dev.mkeeda.arranger.richtext.editor.RichTextState

private var uuid: Long = 1

@Immutable
data class Tool(
    val imageVector: ImageVector? = null,
    val getIcon: @Composable () -> Painter? = { null },
    val enabled: Boolean = false,
    val onClick: (richTextState: RichTextState, Notes) -> Unit,
    val key: Long = uuid++,
    val highlight: Boolean = true,
    val text: String = "",
    val showConfirmDialog: Boolean = false,
    val title: String = "",
    val message: String = "",
)

@Immutable
data class ToolCollection(
    val list: List<Tool>,
)

@Immutable
data class Tools(
    val collection: List<ToolCollection>,
)

fun getToolsList(interactor: Interactor): Tools {
    // ////////////////////////////////
    // Construct editor tool pane
    // ////////////////////////////////

    class ToolsBuilder {
        private val list = mutableListOf<ToolCollection>()

        fun addToolList(vararg tool: Tool) {
            list.add(ToolCollection(listOf(*tool)))
        }

        fun addTool(tool: Tool) {
            list.add(ToolCollection(listOf(tool)))
        }

        fun build() = list
    }

    val builder = ToolsBuilder()

    if (AppSettings.editorBackEnabled) {
        builder.addTool(
            Tool(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                onClick = { _, _ ->
                    interactor.onEditorNavBack()
                },
                highlight = false,
            ),
        )
    }

    builder.addTool(
        Tool(
            imageVector = Undo,
            onClick = { state, note ->
                interactor.sendEditorCommand(Command.Undo(), state)
            },
            highlight = false,
        ),
    )

    builder.addTool(
        Tool(
            imageVector = Redo,
            onClick = { state, note ->
                interactor.sendEditorCommand(Command.Redo(), state)
            },
            highlight = false,
        ),
    )

    builder.addTool(
        Tool(
            imageVector = SAVE_ICON,
            onClick = { state, note ->
                interactor.saveNote(state, note)
            },
            highlight = false,
            showConfirmDialog = true,
            title = "Confirm save action",
            message = "Do you want to save this note ?",
        ),
    )

    builder.addTool(
        Tool(
            imageVector = Icons.Outlined.DeleteForever,
            onClick = { state, note ->
                interactor.deleteNote(note)
            },
            highlight = false,
            showConfirmDialog = true,
            title = "Confirm delete action",
            message = "Do you want to delete this note ?",
        ),
    )

    // Disabled for now
    // TODO: After user cleared editor, undo action didn't work
//    builder.addTool(
//        Tool(
//            imageVector = CLEAR_ALL,
//            onClick = { state, note ->
//                interactor.sendEditorCommand(Command.ClearText(), state)
//            },
//            highlight = false,
//        )
//    )

    builder.addToolList(
        Tool(
            getIcon = { toPainter(h1FormatIcon) },
            onClick = { state, note ->
                interactor.sendEditorCommand(Command.HFormat(HeadingLevel.H1), state)
            },
            text = "size",
        ),
        Tool(
            getIcon = { toPainter(h2FormatIcon) },
            onClick = { state, note ->
                interactor.sendEditorCommand(Command.HFormat(HeadingLevel.H2), state)
            },
            text = "size",
        ),
        Tool(
            getIcon = { toPainter(h3FormatIcon) },
            onClick = { state, note ->
                interactor.sendEditorCommand(Command.HFormat(HeadingLevel.H3), state)
            },
            text = "size",
        ),
        Tool(
            getIcon = { toPainter(h4FormatIcon) },
            onClick = { state, note ->
                interactor.sendEditorCommand(Command.HFormat(HeadingLevel.H4), state)
            },
            text = "size",
        ),
        Tool(
            getIcon = { toPainter(h5FormatIcon) },
            onClick = { state, note ->
                interactor.sendEditorCommand(Command.HFormat(HeadingLevel.H5), state)
            },
            text = "size",
        ),
        Tool(
            getIcon = { toPainter(h6FormatIcon) },
            onClick = { state, note ->
                interactor.sendEditorCommand(Command.HFormat(HeadingLevel.H6), state)
            },
            text = "size",
        ),
    )

    builder.addToolList(
        Tool(
            imageVector = Icons.Outlined.FormatBold,
            onClick = { state, note ->
                interactor.sendEditorCommand(Command.StringFormat(BoldKey), state)
            },
            text = "Bold",
        ),
        Tool(
            imageVector = Icons.Outlined.FormatItalic,
            onClick = { state, note ->
                interactor.sendEditorCommand(Command.StringFormat(ItalicKey), state)
            },
            text = "Italic",
        ),
        Tool(
            imageVector = Icons.Outlined.FormatUnderlined,
            onClick = { state, note ->
                interactor.sendEditorCommand(Command.StringFormat(UnderlineKey), state)
            },
            text = "Underlined",
        ),
        Tool(
            imageVector = Icons.Outlined.StrikethroughS,
            onClick = { state, note ->
                interactor.sendEditorCommand(Command.StringFormat(StrikethroughKey), state)
            },
            text = "Strike through",
        ),
    )

    builder.addToolList(
        Tool(
            imageVector = Icons.Outlined.FormatAlignCenter,
            onClick = { state, note ->
                interactor.sendEditorCommand(Command.TextAlign(TextAlignment.Center), state)
            },
            text = "Align center",
        ),
        Tool(
            imageVector = Icons.AutoMirrored.Outlined.FormatAlignLeft,
            onClick = { state, note ->
                interactor.sendEditorCommand(Command.TextAlign(TextAlignment.Left), state)
            },
            text = "Align left",
        ),
        Tool(
            imageVector = Icons.AutoMirrored.Outlined.FormatAlignRight,
            onClick = { state, note ->
                interactor.sendEditorCommand(Command.TextAlign(TextAlignment.Right), state)
            },
            text = "Align right",
        ),
    )

    builder.addToolList(
        Tool(
            imageVector = List,
            onClick = { state, note ->
                interactor.sendEditorCommand(Command.List(BulletListKey, ListIndentLevel.Level1), state)
            },
            text = "Simple list",
        ),
        Tool(
            imageVector = Format_list_numbered,
            onClick = { state, note ->
                interactor.sendEditorCommand(Command.List(OrderedListKey, ListIndentLevel.Level1), state)
            },
            text = "Numbered list",
        ),
    )

    return Tools(builder.build())
}
