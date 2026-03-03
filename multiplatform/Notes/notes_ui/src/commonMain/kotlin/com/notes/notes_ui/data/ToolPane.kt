package com.notes.notes_ui.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.FormatAlignLeft
import androidx.compose.material.icons.automirrored.outlined.FormatAlignRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.mohamedrejeb.richeditor.model.RichTextState
import api.data.Notes
import com.notes.notes_ui.Interactor
import com.notes.notes_ui.editor.EditorState
import com.notes.notes_ui.editor.AlignCommand
import com.notes.notes_ui.editor.ClearCommand
import com.notes.notes_ui.editor.EditCommand
import com.notes.notes_ui.editor.OrderedListCommand
import com.notes.notes_ui.editor.UnorderedListCommand
import com.notes.ui.*

private var uuid: Long = 1

@Stable
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

@Stable
data class ToolsPane(
    val list: List<Tool>,
)

fun getToolsList(interactor: Interactor): List<ToolsPane> {

    // ////////////////////////////////
    // Construct editor tool pane
    // ////////////////////////////////

    class ToolsBuilder {

        private val list = mutableListOf<ToolsPane>()

        fun addToolList(vararg tool: Tool) {
            list.add(ToolsPane(listOf(*tool)))
        }

        fun addTool(tool: Tool) {
            list.add(ToolsPane(listOf(tool)))
        }

        fun build() = list
    }

    val builder = ToolsBuilder()

    builder.addTool(
        Tool(
            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
            onClick = { _, _ ->
                interactor.onNoteNavigateBack()
            },
            highlight = false,
        )
    )

    builder.addTool(
        Tool(
            imageVector = Undo,
            onClick = { state, note ->
                interactor.redoUndoAction.undoAction()
            },
            highlight = false,
        )
    )

    builder.addTool(
        Tool(
            imageVector = Redo,
            onClick = { state, note ->
                interactor.redoUndoAction.reapplyAction()
            },
            highlight = false,
        )
    )

    builder.addTool(
        Tool(
            imageVector = SAVE_ICON,
            onClick = { state, note ->
                val wrappedState =
                    object : EditorState {
                        override fun getHtml() = state.toHtml()
                    }
                interactor.saveContent(wrappedState, note)
            },
            highlight = false,
            showConfirmDialog = true,
            title = "Confirm save action",
            message = "Do you want to save this note ?",
        )
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
        )
    )

    builder.addTool(
        Tool(
            imageVector = CLEAR_ALL,
            onClick = { state, note ->
                interactor.sendEditorCommand(ClearCommand(state))
            },
            highlight = false,
        )
    )

    builder.addToolList(
        Tool(
            getIcon = { getIconByKey(h1FormatIcon) },
            onClick = { state, note ->
                interactor.sendEditorCommand(
                    EditCommand(
                        spanStyle = SpanStyle(fontSize = 50.sp),
                        richTextState = state,
                    ),
                )
            },
            text = "size",
        ),
        Tool(
            getIcon = { getIconByKey(h2FormatIcon) },
            onClick = { state, note ->
                interactor.sendEditorCommand(
                    EditCommand(
                        spanStyle = SpanStyle(fontSize = 40.sp),
                        richTextState = state,
                    ),
                )
            },
            text = "size",
        ),
        Tool(
            getIcon = { getIconByKey(h3FormatIcon) },
            onClick = { state, note ->
                interactor.sendEditorCommand(
                    EditCommand(
                        spanStyle = SpanStyle(fontSize = 30.sp),
                        richTextState = state,
                    ),
                )
            },
            text = "size",
        ),
        Tool(
            getIcon = { getIconByKey(h4FormatIcon) },
            onClick = { state, note ->
                interactor.sendEditorCommand(
                    EditCommand(
                        spanStyle = SpanStyle(fontSize = 20.sp),
                        richTextState = state,
                    ),
                )
            },
            text = "size",
        ),
        Tool(
            getIcon = { getIconByKey(h5FormatIcon) },
            onClick = { state, note ->
                interactor.sendEditorCommand(
                    EditCommand(
                        spanStyle = SpanStyle(fontSize = 15.sp),
                        richTextState = state,
                    ),
                )
            },
            text = "size",
        ),
        Tool(
            getIcon = { getIconByKey(h6FormatIcon) },
            onClick = { state, note ->
                interactor.sendEditorCommand(
                    EditCommand(
                        spanStyle = SpanStyle(fontSize = 10.sp),
                        richTextState = state,
                    ),
                )
            },
            text = "size",
        )
    )

    builder.addToolList(
        Tool(
            imageVector = Icons.Outlined.FormatBold,
            onClick = { state, note ->
                interactor.sendEditorCommand(
                    EditCommand(
                        spanStyle = SpanStyle(fontWeight = FontWeight.Bold),
                        richTextState = state,
                    ),
                )
            },
            text = "Bold",
        ),
        Tool(
            imageVector = Icons.Outlined.FormatItalic,
            onClick = { state, note ->
                interactor.sendEditorCommand(
                    EditCommand(
                        spanStyle = SpanStyle(fontStyle = FontStyle.Italic),
                        richTextState = state,
                    ),
                )
            },
            text = "Italic",
        ),
        Tool(
            imageVector = Icons.Outlined.FormatUnderlined,
            onClick = { state, note ->
                interactor.sendEditorCommand(
                    EditCommand(
                        spanStyle = SpanStyle(textDecoration = TextDecoration.Underline),
                        richTextState = state,
                    ),
                )
            },
            text = "Underlined",
        ),
        Tool(
            imageVector = Icons.Outlined.StrikethroughS,
            onClick = { state, note ->
                interactor.sendEditorCommand(
                    EditCommand(
                        spanStyle = SpanStyle(textDecoration = TextDecoration.LineThrough),
                        richTextState = state,
                    ),
                )
            },
            text = "Strike through",
        )
    )

    builder.addToolList(
        Tool(
            imageVector = Icons.Outlined.FormatAlignCenter,
            onClick = { state, note ->
                interactor.sendEditorCommand(
                    AlignCommand(
                        paragraphStyle = ParagraphStyle(textAlign = TextAlign.Center),
                        richTextState = state,
                    ),
                )
            },
            text = "Align center",
        ),
        Tool(
            imageVector = Icons.AutoMirrored.Outlined.FormatAlignLeft,
            onClick = { state, note ->
                interactor.sendEditorCommand(
                    AlignCommand(
                        paragraphStyle = ParagraphStyle(textAlign = TextAlign.Left),
                        richTextState = state,
                    ),
                )
            },
            text = "Align left",
        ),
        Tool(
            imageVector = Icons.AutoMirrored.Outlined.FormatAlignRight,
            onClick = { state, note ->
                interactor.sendEditorCommand(
                    AlignCommand(
                        paragraphStyle = ParagraphStyle(textAlign = TextAlign.Right),
                        richTextState = state,
                    ),
                )
            },
            text = "Align right",
        )
    )

    builder.addToolList(
        Tool(
            imageVector = List,
            onClick = { state, note ->
                interactor.sendEditorCommand(
                    UnorderedListCommand(state),
                )
            },
            text = "Simple list",
        ),
        Tool(
            imageVector = Format_list_numbered,
            onClick = { state, note ->
                interactor.sendEditorCommand(
                    OrderedListCommand(state),
                )
            },
            text = "Numbered list",
        )
    )

    return builder.build()
}
