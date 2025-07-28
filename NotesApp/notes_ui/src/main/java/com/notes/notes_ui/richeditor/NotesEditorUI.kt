package com.notes.notes_ui.richeditor

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.notes.ui.theme.AppTheme

@Composable
fun NotesEditorUI(modifier: Modifier = Modifier) {
    AppTheme {
        RichTextEditorUI(modifier)
    }
}

@Preview
@Preview(
    uiMode = UI_MODE_TYPE_NORMAL or UI_MODE_NIGHT_YES,
    device = "spec:parent=pixel_5,orientation=landscape"
)
@Composable
fun NotesEditorUIPrev(modifier: Modifier = Modifier) {
    NotesEditorUI()
}

@Composable
private fun RichTextEditorUI(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier
    ) { innerPadding ->

        val state = rememberRichTextState()

        RichTextEditor(
            state = state,
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        )

    }

}