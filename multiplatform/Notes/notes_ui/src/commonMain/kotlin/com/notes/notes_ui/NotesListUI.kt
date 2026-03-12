package com.notes.notes_ui

import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import api.Platform
import api.data.AppSettings
import api.data.Notes
import api.data.NotesCollection
import com.notes.notes_ui.editor.createEditorState
import com.notes.ui.StyledChip
import dev.mkeeda.arranger.richtext.RichString
import dev.mkeeda.arranger.richtext.editor.RichTextState

@Composable
fun NotesListUI(
    modifier: Modifier = Modifier,
    addAction: () -> Unit,
    onSelected: (Notes) -> Unit,
    notes: NotesCollection,
    isPhoneSize: Boolean,
    topBar: @Composable () -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar =  { topBar() },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.imePadding(),
                onClick = { addAction() },
            ) {
                Icon(Icons.Filled.Add, null)
            }
        },
    ) { innerPadding ->
        NotesList(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .testTag("notes_list"),
            notes = notes,
            onSelected = onSelected,
            isPhoneSize = isPhoneSize,
        )
    }
}

@Composable
fun NotesList(
    modifier: Modifier = Modifier,
    notes: NotesCollection,
    onSelected: (Notes) -> Unit,
    isPhoneSize: Boolean,
) {
    if (notes.collection.isEmpty()) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            SuggestionChip(
                onClick = {},
                label = {
                    Text("Create your first note by tapping '+' button")
                },
            )
        }
    } else {
        if (isPhoneSize) {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
            ) {
                for (note in notes.collection) {
                    item(key = note.id) {
                        Platform().logger.logi("LazyColumn: adding key = ${note.id}")
                        EditorPreviewStateful(note = note) {
                            onSelected(note)
                        }
                    }
                }
            }
        } else {
            LazyVerticalStaggeredGrid(
                modifier = modifier,
                columns = StaggeredGridCells.Adaptive(160.dp),
            ) {
                for (note in notes.collection) {
                    item(key = note.id) {
                        EditorPreviewStateful(note = note) {
                            onSelected(note)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditorPreviewStateful(
    note: Notes,
    onClick: () -> Unit,
) {
    var title by rememberSaveable {
        mutableStateOf("")
    }

    val state =
        remember(note.content) {
            createEditorState(note)
        }

    LaunchedEffect(note.content) {
        // Get first line as a title
        val textContent = (note.richString as RichString).text
        val firstLine = textContent.substringBefore('\n')
        title =
            if (AppSettings.isDebugEnabled) {
                "[id=${note.id}] $firstLine"
            } else {
                firstLine
            }
    }

    EditorPreview(state = state, title = title) {
        onClick()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorPreview(
    state: RichTextState,
    title: String,
    onClicked: () -> Unit,
) {
    val contentModifier =
        Modifier
            .fillMaxWidth()
            .heightIn(max = 250.dp)
            .padding(4.dp)

    Box(
        modifier =
            contentModifier.background(
                shape = RoundedCornerShape(10),
                color = MaterialTheme.colorScheme.surface,
            ),
    ) {
        // Readonly field doesn't react on click events
        EditorLayout(state = state, readOnly = true)

        StyledChip(title)

        // Composable to be able to intercept click events
        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .clickable {
                        onClicked()
                    },
        )
    }
}
