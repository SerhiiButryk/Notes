package com.notes.notes_ui

import android.os.Parcelable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FormatBold
import androidx.compose.material.icons.outlined.FormatItalic
import androidx.compose.material.icons.outlined.FormatUnderlined
import androidx.compose.material.icons.outlined.StrikethroughS
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.ViewModel
import com.mohamedrejeb.richeditor.model.RichTextState
import com.notes.ui.CLEAR_ALL
import com.notes.ui.SAVE_ICON
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

class NotesViewModel : ViewModel() {

    // This annotation could be redundant as
    // the class is already stable, because all properties are stable.
    // However, keep it for clarity.
    @Stable
    @Parcelize
    data class Notes(val content: String = "", val id: String = Random.nextLong(1000).toString()) :
        Parcelable {

        companion object {
            fun EmptyNote() = Notes("", "")
        }
    }

    @Stable
    data class ToolsPane(
        val imageVector: ImageVector? = null,
        val id: Int = 0,
        val enabled: Boolean = false,
        val onClick: (richTextState: RichTextState) -> Unit = {},
        val key: Long = uuid++
    ) {
        companion object {
            private var uuid: Long = 0
        }
    }

    private val _notesState = MutableStateFlow(listOf<Notes>())
    val notesState = _notesState.asStateFlow()

    init {

        val fakeList = listOf(
            Notes("Note 1 \n Some note 1"),
            Notes("Note 2 \n Some note 2"),
            Notes(
                "Note 3 \n Some note 4 Some note 4  Some note 4 \n" +
                        "Some note 4 Some note 4 \n"
            ),
            Notes(
                "Note 4 \n Some note 4 Some note 4  Some note 4 \nSome note 4 Some note 4 \nSome note 4 Some note 4  \n" +
                        " Some note 4 Some note 4  Some note 4 \n" +
                        "Some note 4 Some note 4 \n" +
                        "Some note 4 Some note 4 "
            )
        )

        _notesState.value = fakeList
    }

    fun getToolsPaneItems(): List<ToolsPane> {
        val list = mutableListOf<ToolsPane>()
        list.add(ToolsPane(
            imageVector = SAVE_ICON,
            onClick = {}
        ))
        list.add(ToolsPane(
            imageVector = CLEAR_ALL,
            onClick = {}
        ))
        list.add(ToolsPane(
            id = R.drawable.format_h1,
            onClick = {}
        ))
        list.add(ToolsPane(
            id = R.drawable.format_h2,
            onClick = {}
        ))
        list.add(ToolsPane(
            id = R.drawable.format_h3,
            onClick = {}
        ))
        list.add(ToolsPane(
            id = R.drawable.format_h4,
            onClick = {}
        ))
        list.add(ToolsPane(
            id = R.drawable.format_h5,
            onClick = {}
        ))
        list.add(ToolsPane(
            id = R.drawable.format_h6,
            onClick = {}
        ))
        list.add(ToolsPane(
            imageVector = Icons.Outlined.FormatBold,
            onClick = { state ->
                // Simulate toggle behavior. Adding or removing style.
                val style = SpanStyle(fontWeight = FontWeight.Bold)
                if (state.currentSpanStyle.fontWeight == FontWeight.Bold) {
                    state.removeSpanStyle(style)
                } else {
                    state.toggleSpanStyle(style)
                }
            }
        ))
        list.add(ToolsPane(
            imageVector = Icons.Outlined.FormatItalic,
            onClick = {}
        ))
        list.add(ToolsPane(
            imageVector = Icons.Outlined.FormatUnderlined,
            onClick = {}
        ))
        list.add(ToolsPane(
            imageVector = Icons.Outlined.StrikethroughS,
            onClick = {}
        ))
        return list
    }
}