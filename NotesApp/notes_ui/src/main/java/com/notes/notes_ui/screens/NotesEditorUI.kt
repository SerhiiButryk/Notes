package com.notes.notes_ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults.richTextEditorColors
import com.notes.notes_ui.NotesViewModel
import com.notes.notes_ui.screens.components.ToolsPane
import com.notes.notes_ui.screens.editor.ToolsPane

@Composable
fun NotesEditorUI(
    modifier: Modifier = Modifier,
    notes: NotesViewModel.Notes,
    state: RichTextState,
    toolsPaneItems: List<ToolsPane> = emptyList<ToolsPane>()
) {
    EditorUI(modifier, notes, state, toolsPaneItems)
}

@Composable
private fun EditorUI(
    modifier: Modifier = Modifier,
    notes: NotesViewModel.Notes,
    state: RichTextState,
    toolsPaneItems: List<ToolsPane>
) {
    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        // Adds cross fade animation when selecting a note from the list
        Crossfade(
            targetState = notes,
            label = "Editor cross fade animation",
            modifier = Modifier.padding(innerPadding)
        ) { note ->
            if (note == NotesViewModel.Notes.AbsentNote()) {
                InfoLabel()
            } else {
                EditorLayout(state = state, toolsPaneItems = toolsPaneItems, notes = note)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorLayout(
    modifier: Modifier = Modifier,
    state: RichTextState,
    toolsPaneItems: List<ToolsPane>,
    notes: NotesViewModel.Notes,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        RichTextEditor(
            state = state,
            modifier = Modifier
                .focusable()
                .fillMaxSize()
                // add weight modifier to the composable to ensure
                // that the composable is measured after the other
                // composable is measured specifically after the tools pane.
                .weight(1f),
            colors = richTextEditorColors(
                // Remove bottom thin line
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(4),
            onTextChanged = {
            }
        )

        ToolsPane(state = state, toolsPaneItems = toolsPaneItems, notes = notes)
    }
}

@Composable
private fun InfoLabel(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Select an item")
    }
}

@Preview
@Preview(
    uiMode = UI_MODE_TYPE_NORMAL or UI_MODE_NIGHT_YES,
    device = "spec:parent=pixel_5,orientation=landscape"
)
@Composable
fun NotesEditorUIPrev(modifier: Modifier = Modifier) {
    val state = rememberRichTextState()
    NotesEditorUI(notes = NotesViewModel.Notes.NewNote(), state = state, toolsPaneItems = emptyList())
}