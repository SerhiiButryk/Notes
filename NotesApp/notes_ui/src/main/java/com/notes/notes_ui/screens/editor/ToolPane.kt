package com.notes.notes_ui.screens.editor

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatAlignLeft
import androidx.compose.material.icons.automirrored.outlined.FormatAlignRight
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.FormatAlignCenter
import androidx.compose.material.icons.outlined.FormatBold
import androidx.compose.material.icons.outlined.FormatItalic
import androidx.compose.material.icons.outlined.FormatUnderlined
import androidx.compose.material.icons.outlined.StrikethroughS
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.mohamedrejeb.richeditor.model.RichTextState
import com.notes.notes_ui.EditorState
import com.notes.notes_ui.Interaction
import com.notes.notes_ui.NotesViewModel
import com.notes.notes_ui.R
import com.notes.ui.CLEAR_ALL
import com.notes.ui.SAVE_ICON

private var uuid: Long = 0

@Stable
data class Tool(
    val imageVector: ImageVector? = null,
    val id: Int = 0,
    val enabled: Boolean = false,
    val onClick: (richTextState: RichTextState, NotesViewModel.Notes) -> Unit,
    val key: Long = uuid++,
    val highlight: Boolean = true,
    val text: String = ""
)

@Stable
data class ToolsPane(
    val list: List<Tool>
)

fun getToolsList(notesInteraction: Interaction): List<ToolsPane> {
    //////////////////////////////////
    // Construct editor tool pane
    //////////////////////////////////
    val list = listOf(
        ToolsPane(
            listOf(
                Tool(
                    imageVector = SAVE_ICON,
                    onClick = { state, note ->
                        val wrappedState = object : EditorState {
                            override fun getHtml() = state.toHtml()
                        }
                        notesInteraction.saveContent(wrappedState, note)
                    },
                    highlight = false
                )
            )
        ),
        ToolsPane(listOf(
                Tool(
                    imageVector = Icons.Outlined.DeleteForever,
                    onClick = { state, note ->
                        notesInteraction.deleteNote(note)
                    },
                    highlight = false
                )
            )
        ),
        ToolsPane(
            listOf(
                Tool(
                    imageVector = CLEAR_ALL,
                    onClick = { state, note ->
                        notesInteraction.sendEditorCommand(ClearCommand(state))
                    },
                    highlight = false
                )
            )
        ),
        ToolsPane(
            listOf(
                Tool(
                    id = R.drawable.format_h1,
                    onClick = { state, note ->
                        notesInteraction.sendEditorCommand(
                            EditCommand(
                                spanStyle = SpanStyle(fontSize = 50.sp),
                                richTextState = state
                            )
                        )
                    },
                    text = "size"),
                Tool(
                    id = R.drawable.format_h2,
                    onClick = { state, note ->
                        notesInteraction.sendEditorCommand(
                            EditCommand(
                                spanStyle = SpanStyle(fontSize = 40.sp),
                                richTextState = state
                            )
                        )
                    },
                    text = "size"
                ),
                Tool(
                    id = R.drawable.format_h3,
                    onClick = { state, note ->
                        notesInteraction.sendEditorCommand(
                            EditCommand(
                                spanStyle = SpanStyle(fontSize = 30.sp),
                                richTextState = state
                            )
                        )
                    },
                    text = "size"),
                Tool(
                    id = R.drawable.format_h4,
                    onClick = { state, note ->
                        notesInteraction.sendEditorCommand(
                            EditCommand(
                                spanStyle = SpanStyle(fontSize = 20.sp),
                                richTextState = state
                            )
                        )
                    },
                    text = "size"),
                Tool(
                    id = R.drawable.format_h5,
                    onClick = { state, note ->
                        notesInteraction.sendEditorCommand(
                            EditCommand(
                                spanStyle = SpanStyle(fontSize = 15.sp),
                                richTextState = state
                            )
                        )
                    },
                    text = "size"),
                Tool(
                    id = R.drawable.format_h6,
                    onClick = { state, note ->
                        notesInteraction.sendEditorCommand(
                            EditCommand(
                                spanStyle = SpanStyle(fontSize = 10.sp),
                                richTextState = state
                            )
                        )
                    },
                    text = "size"
                )
            )
        ),
        ToolsPane(
            listOf(
                Tool(
                    imageVector = Icons.Outlined.FormatBold,
                    onClick = { state, note ->
                        notesInteraction.sendEditorCommand(
                            EditCommand(
                                spanStyle = SpanStyle(fontWeight = FontWeight.Bold),
                                richTextState = state
                            )
                        )
                    },
                    text = "Bold"),
                Tool(
                    imageVector = Icons.Outlined.FormatItalic,
                    onClick = { state, note ->
                        notesInteraction.sendEditorCommand(
                            EditCommand(
                                spanStyle = SpanStyle(fontStyle = FontStyle.Italic),
                                richTextState = state
                            )
                        )
                    },
                    text = "Italic"),
                Tool(
                    imageVector = Icons.Outlined.FormatUnderlined,
                    onClick = { state, note ->
                        notesInteraction.sendEditorCommand(
                            EditCommand(
                                spanStyle = SpanStyle(textDecoration = TextDecoration.Underline),
                                richTextState = state
                            )
                        )
                    },
                    text = "Underlined"
                ),
                Tool(
                    imageVector = Icons.Outlined.StrikethroughS,
                    onClick = { state, note ->
                        notesInteraction.sendEditorCommand(
                            EditCommand(
                                spanStyle = SpanStyle(textDecoration = TextDecoration.LineThrough),
                                richTextState = state
                            )
                        )
                    },
                    text = "Strike through"
                )
            )
        ),
        ToolsPane(
            listOf(
                Tool(
                    imageVector = Icons.Outlined.FormatAlignCenter,
                    onClick = { state, note ->
                        notesInteraction.sendEditorCommand(
                            AlignCommand(
                                paragraphStyle = ParagraphStyle(textAlign = TextAlign.Center),
                                richTextState = state
                            )
                        )
                    },
                    text = "Align center"
                ),
                Tool(
                    imageVector = Icons.AutoMirrored.Outlined.FormatAlignLeft,
                    onClick = { state, note ->
                        notesInteraction.sendEditorCommand(
                            AlignCommand(
                                paragraphStyle = ParagraphStyle(textAlign = TextAlign.Left),
                                richTextState = state
                            )
                        )
                    },
                    text = "Align left"
                ),
                Tool(
                    imageVector = Icons.AutoMirrored.Outlined.FormatAlignRight,
                    onClick = { state, note ->
                        notesInteraction.sendEditorCommand(
                            AlignCommand(
                                paragraphStyle = ParagraphStyle(textAlign = TextAlign.Right),
                                richTextState = state
                            )
                        )
                    },
                    text = "Align right"
                )
            )
        ),
        ToolsPane(
            listOf(
                Tool(
                    imageVector = com.notes.ui.List,
                    onClick = { state, note ->
                        notesInteraction.sendEditorCommand(
                            UnorderedListCommand(state)
                        )
                    },
                    text = "Simple list"
                ),
                Tool(
                    imageVector = com.notes.ui.Format_list_numbered,
                    onClick = { state, note ->
                        notesInteraction.sendEditorCommand(
                            OrderedListCommand(state)
                        )
                    },
                    text = "Numbered list"
                )
            )
        )
    )

    return list
}