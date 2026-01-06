package com.notes.notes_ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults.richTextEditorColors
import com.notes.api.data.Notes
import com.notes.notes_ui.EditorCommand
import com.notes.notes_ui.screens.components.ToolsBar
import com.notes.notes_ui.screens.editor.TextInputCommand
import com.notes.notes_ui.screens.editor.ToolsPane
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun NotesEditorUI(
    modifier: Modifier = Modifier,
    notes: Notes,
    state: RichTextState,
    toolsPaneItems: List<ToolsPane> = emptyList(),
    onTextChanged: (EditorCommand) -> Unit = {},
) {
    EditorUI(modifier, notes, state, toolsPaneItems, onTextChanged)
}

@Composable
private fun EditorUI(
    modifier: Modifier = Modifier,
    notes: Notes,
    state: RichTextState,
    toolsPaneItems: List<ToolsPane>,
    onTextChanged: (EditorCommand) -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        // Adds cross fade animation when selecting a note from the list
        Crossfade(
            targetState = notes,
            label = "Editor cross fade animation",
            modifier =
                Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .imePadding(),
        ) { note ->
            if (note == Notes.AbsentNote()) {
                InfoLabel()
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    EditorLayout(
                        state = state,
                        // add weight modifier to the composable to ensure
                        // that the composable is measured after the other
                        // composable is measured specifically after the tools pane.
                        modifier = Modifier.weight(1f),
                        onTextChanged = onTextChanged,
                    )
                    ToolsBar(
                        state = state,
                        toolsPaneItems = toolsPaneItems,
                        notes = notes,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorLayout(
    state: RichTextState,
    modifier: Modifier,
    onTextChanged: (EditorCommand) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    RichTextEditor(
        state = state,
        modifier =
            Modifier
                .focusable()
                .fillMaxSize()
                .then(modifier),
        colors =
            richTextEditorColors(
                // Remove bottom thin line
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
        shape = RoundedCornerShape(4),
        onTextChanged = { old ->
            coroutineScope.launch {
                // Some delay is necessary because we don't get current UI state immediately
                // Might be a better way, but just a simple fix for now
                delay(100)
                val new = state.toHtml() // Getting the current UI state
                val command = TextInputCommand(new, old, state)
                onTextChanged(command)
            }
        },
    )
}

@Composable
private fun InfoLabel(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize(),
    ) {
        Text("Select an item")
    }
}

@Preview
@Preview(
    uiMode = UI_MODE_TYPE_NORMAL or UI_MODE_NIGHT_YES,
    device = "spec:parent=pixel_5,orientation=landscape",
)
@Composable
fun NotesEditorUIPrev() {
    val state = rememberRichTextState()
    NotesEditorUI(
        notes = Notes.NewNote(),
        state = state,
        toolsPaneItems = emptyList(),
    )
}
