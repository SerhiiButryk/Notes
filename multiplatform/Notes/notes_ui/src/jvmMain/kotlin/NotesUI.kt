import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import api.data.Notes
import api.data.NotesCollection
import com.notes.notes_ui.NotesEditorUI
import com.notes.notes_ui.NotesListUI
import com.notes.notes_ui.components.NotesNavRail
import com.notes.notes_ui.models.Tools
import com.notes.notes_ui.editor.createEditorState
import com.notes.ui.SearchBarFieldV2
import kotlinx.coroutines.launch
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import java.awt.Cursor

@OptIn(ExperimentalSplitPaneApi::class, ExperimentalMaterial3Api::class)
@Composable
@Preview
fun NotesScreenImpl(
    onSettingsClick: () -> Unit = {},
    noteList: NotesCollection = NotesCollection(),
    note: Notes = Notes(),
    onSelectAction: (Notes) -> Unit = {},
    onAddAction: () -> Unit = {},
    tools: Tools = Tools(emptyList()),
) {

    val coroutineScope = rememberCoroutineScope()

    var state by remember(note.content) {
        mutableStateOf(createEditorState(note))
    }

    val splitterState = rememberSplitPaneState(initialPositionPercentage = 0.3f)

    Row {

        NotesNavRail(onSettingsClick = onSettingsClick)

        HorizontalSplitPane(splitPaneState = splitterState) {
            // The Notes List UI
            first(minSize = 150.dp) {
                NotesListUI(
                    notes = noteList,
                    onSelected = { selectedNote ->
                        // Open Note Editor Screen
                        coroutineScope.launch {
                            state = createEditorState(selectedNote)
                            onSelectAction(selectedNote)
                        }
                    },
                    addAction = {
                        // Open Note Editor Screen
                        coroutineScope.launch {
                            state = createEditorState(Notes())
                            onAddAction()
                        }
                    },
                    isPhoneSize = false,
                    topBar = {
                        SearchBarFieldV2()
                    }
                )
            }

            // The "Second" pane (Right side)
            second(minSize = 200.dp) {
                val bottomSheetState =
                    rememberModalBottomSheetState(
                        skipPartiallyExpanded = false,
                    )

                NotesEditorUI(
                    notes = note,
                    state = state,
                    tools = tools,
                    bottomSheetState = bottomSheetState,
                    showFolderButton = false,
                )
            }

            // The Editor UI
            splitter {
                visiblePart {
                    Box(
                        Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.outline),
                    )
                }
                handle {
                    Box(
                        Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .markAsHandle() // This makes it draggable
                            .cursorForHorizontalResize() // Sets the mouse icon automatically
                            .width(8.dp)
                            .fillMaxHeight(),
                    )
                }
            }
        }
    }
}

private fun Modifier.cursorForHorizontalResize(): Modifier =
    pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))

