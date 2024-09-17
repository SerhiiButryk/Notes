/*
 * Copyright 2024. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.serhii.apps.notes.ui.data_model.NoteModel
import com.serhii.apps.notes.ui.state_holders.NotesViewModel

/**
 * Notes editor screen
 */

@Composable
fun NotesEditorUI(
    note: NoteModel = NoteModel(),
    viewModel: NotesViewModel? = null,
    menuOptionsList: List<MenuOptions> = emptyList(),
    uiState: NotesViewModel.NotesEditorUIState,
) {

    Scaffold(
        topBar = {
            SearchUI(
                hint = "Search text here",
                hasBackButton = true,
                backAction = { viewModel?.navigateBack() },
                menuOptionsList = menuOptionsList
            )
        },
        bottomBar = {
            BottomBarUI(viewModel = viewModel, note = note)
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            val state = rememberRichTextState()

            val inputFocusEditor = remember { FocusRequester() }

            RichTextEditor(
                textStyle = MaterialTheme.typography.bodyMedium,
                state = state,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = 4.dp)
                    .focusRequester(inputFocusEditor)
                    .clip(RoundedCornerShape(16.dp)),
            )

            viewModel?.saveEditorState(state)
            viewModel?.requestKeyboard(inputFocusEditor)
        }
    }

    val openDialog = uiState.openDialog
    if (openDialog && !uiState.dialogState.isOpen) {
        BasicDialogUI(dialogState = uiState.dialogState)
    }
}

@Composable
fun BottomBarUI(viewModel: NotesViewModel?, note: NoteModel) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceDim),

        ) {

        val modifier = Modifier.size(30.dp)

        IconButton(onClick = {

            val editor = viewModel?.editorState

            if (editor != null) {

                val textNote = editor.toText()
                note.note = textNote

                viewModel.saveNote(note)
            }
        }) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = null,
                modifier = modifier
            )
        }

        IconButton(onClick = { viewModel?.deleteNote(note) }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                modifier = modifier
            )
        }

        IconButton(onClick = { viewModel?.backupNote(note) }) {
            Icon(
                imageVector = Icons.Default.Backup,
                contentDescription = null,
                modifier = modifier
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotesEditorUIPreview() {
    NotesEditorUI(uiState = NotesViewModel.NotesEditorUIState(NoteModel()))
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun NotesEditorUIPreviewDark() {
    NotesEditorUI(uiState = NotesViewModel.NotesEditorUIState(NoteModel()))
}