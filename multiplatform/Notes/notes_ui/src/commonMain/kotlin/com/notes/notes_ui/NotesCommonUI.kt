package com.notes.notes_ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import api.data.Notes
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.notes.notes_ui.components.NotesNavRail
import com.notes.notes_ui.data.ToolsPane
import com.notes.notes_ui.editor.EditorCommand
import kotlinx.coroutines.launch

@Composable
fun NotesCommonUI(
    notes: List<Notes>,
    toolsPaneItems: List<ToolsPane>,
    note: Notes,
    onAddAction: suspend () -> Unit,
    onSelectAction: suspend (Notes) -> Unit,
    onTextChanged: (EditorCommand) -> Unit,
    onSettingsClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    showNavRail: Boolean,
    isPhoneSize: Boolean
) {
    Row {

        val coroutineScope = rememberCoroutineScope()
        val state = rememberRichTextState()

        LaunchedEffect(false) {
            // Set text when the editor is open
            state.clear()
            state.setHtml(note.content)
        }

        // Show nav rail for large screens
        if (showNavRail) {
            NotesNavRail(onSettingsClick = onSettingsClick)
        }

        // Note List screen
        Column(
            modifier = Modifier.weight(1f)
        ) {
            NotesListUI(
                notes = notes,
                onSelected = { selectedNote ->
                    // Open Note Editor Screen
                    coroutineScope.launch {
                        state.clear() // Clear editor before opening
                        state.setHtml(selectedNote.content) // Set content
                        onSelectAction(selectedNote)
                    }
                },
                addAction = {
                    // Open Note Editor Screen
                    coroutineScope.launch {
                        state.clear() // Clear editor before opening
                        onAddAction()
                    }
                },
                onSettingsClick = onSettingsClick,
                onBackClick = onBackClick,
                isPhoneSize = isPhoneSize
            )
        }

        Column(
            modifier = Modifier.weight(2f)
        ) {
            NotesEditorUI(
                notes = note,
                state = state,
                toolsPaneItems = toolsPaneItems,
                onTextChanged = onTextChanged,
            )
        }

    }

}