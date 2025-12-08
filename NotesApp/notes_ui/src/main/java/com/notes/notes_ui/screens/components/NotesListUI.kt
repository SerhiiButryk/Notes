package com.notes.notes_ui.screens.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults.richTextEditorColors
import com.notes.notes_ui.NotesViewModel
import com.notes.ui.SearchBarField
import com.notes.ui.isTabletOrFoldableExpanded

@Composable
fun NotesListUI(
    modifier: Modifier = Modifier,
    addAction: () -> Unit,
    onSettingsAction: () -> Unit = {},
    onSelected: (NotesViewModel.Notes) -> Unit,
    notes: List<NotesViewModel.Notes>,
    sizeClass: WindowSizeClass,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SearchBarField(
                trailingIcon = {
                    // Show settings conditionally for phone devices
                    if (!isTabletOrFoldableExpanded(sizeClass)) {
                        IconButton(
                            onClick = { onSettingsAction() }) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                })
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.imePadding(),
                onClick = { addAction() },
            ) {
                Icon(Icons.Filled.Add, null)
            }
        }
    ) { innerPadding ->
        NotesList(
            modifier = Modifier.padding(innerPadding),
            sizeClass = sizeClass,
            notes = notes,
            onSelected = onSelected
        )
    }
}

@Composable
fun NotesList(
    modifier: Modifier = Modifier,
    notes: List<NotesViewModel.Notes>,
    onSelected: (NotesViewModel.Notes) -> Unit,
    sizeClass: WindowSizeClass
) {
    if (notes.isEmpty()) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Create your first note by tapping '+' button")
        }
    } else {
        if (!isTabletOrFoldableExpanded(sizeClass)) {
            LazyColumn(
                modifier = modifier.fillMaxSize()
            ) {
                for (note in notes) {
                    item(key = note.id) {
                        EditorPreviewStateful(content = note.content) { onSelected(note) }
                    }
                }
            }
        } else {
            LazyVerticalStaggeredGrid(
                modifier = modifier,
                columns = StaggeredGridCells.Adaptive(160.dp)
            ) {
                for (note in notes) {
                    item(key = note.id) {
                        EditorPreviewStateful(content = note.content) { onSelected(note) }
                    }
                }
            }
        }
    }

}

@Composable
private fun EditorPreviewStateful(content: String, onClicked: () -> Unit) {

    val state = rememberRichTextState()

    LaunchedEffect(content) {
        state.clear()
        state.setHtml(content)
    }

    EditorPreview(state = state) {
        onClicked()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorPreview(state: RichTextState, onClicked: () -> Unit) {

    val contentModifier = Modifier
        .fillMaxWidth()
        .heightIn(max = 250.dp)

    Box(
        modifier = contentModifier
    ) {

        // Readonly field doesn't react on click events
        RichTextEditor(
            state = state,
            shape = RoundedCornerShape(10),
            readOnly = true,
            colors = richTextEditorColors(
                // Remove bottom thin line
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            modifier = contentModifier
                .padding(4.dp)
        )

        // Composable to be able to intercept click events
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable {
                    onClicked()
                }
        )

    }
}