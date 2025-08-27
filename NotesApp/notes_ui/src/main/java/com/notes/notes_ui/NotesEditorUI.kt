package com.notes.notes_ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.notes.ui.theme.AppTheme

@Composable
fun NotesEditorUI(modifier: Modifier = Modifier, notes: NotesViewModel.Notes?, state: RichTextState) {
    AppTheme {
        EditorUI(modifier, notes, state)
    }
}

@Composable
private fun EditorUI(modifier: Modifier = Modifier, notes: NotesViewModel.Notes?, state: RichTextState) {
    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->

        if (notes == null) {
            InfoLabel()
        } else {
            RichTextEditor(
                state = state,
                modifier = Modifier
                    .focusable()
                    .padding(innerPadding)
                    .fillMaxSize()
            )
        }

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
    NotesEditorUI(notes = NotesViewModel.Notes(), state = state)
}