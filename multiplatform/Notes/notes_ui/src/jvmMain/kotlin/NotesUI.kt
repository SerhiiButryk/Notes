import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.notes.notes_ui.NotesCommonUI
import com.notes.notes_ui.NotesEditorUI
import com.notes.notes_ui.NotesListUI
import com.notes.notes_ui.NotesVM
import kotlinx.coroutines.launch
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import java.awt.Cursor

private fun Modifier.cursorForHorizontalResize(): Modifier =
    pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
@Preview
fun NotesScreenImpl(onSettingsClick: () -> Unit) {

    val viewModel = viewModel { NotesVM() }

    val noteList by viewModel.notesState.collectAsState()
    val note by viewModel.noteState.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val state = rememberRichTextState()

    LaunchedEffect(false) {
        // Set text when the editor is open
        state.clear()
        state.setHtml(note.content)
    }

    val splitterState = rememberSplitPaneState(initialPositionPercentage = 0.3f)

    HorizontalSplitPane(splitPaneState = splitterState) {
        // The "First" pane (Left side)
        first(minSize = 150.dp) {
            NotesListUI(
                notes = noteList,
                onSelected = { selectedNote ->
                    // Open Note Editor Screen
                    coroutineScope.launch {
                        state.clear() // Clear editor before opening
                        state.setHtml(selectedNote.content) // Set content
                        //onSelectAction(selectedNote)
                    }
                },
                addAction = {
                    // Open Note Editor Screen
                    coroutineScope.launch {
                        state.clear() // Clear editor before opening
//                        onAddAction()
                    }
                },
                onSettingsClick = onSettingsClick,
                onBackClick = {},
                isPhoneSize = false
            )
        }

        // The "Second" pane (Right side)
        second(minSize = 200.dp) {
            NotesEditorUI(
                notes = note,
                state = state,
                toolsPaneItems = emptyList(),
                onTextChanged = {},
            )
        }

        // The "Splitter" (The draggable handle)
        splitter {
            visiblePart {
                Box(
                    Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.outline)
                )
            }
            handle {
                Box(
                    Modifier
                        .markAsHandle() // This makes it draggable
                        .cursorForHorizontalResize() // Sets the mouse icon automatically
                        .width(8.dp)
                        .fillMaxHeight()
                )
            }
        }
    }

}