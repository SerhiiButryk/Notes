package com.notes.notes_ui

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import api.Platform
import api.data.Notes
import api.data.NotesCollection
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults.richTextEditorColors
import com.notes.ui.SearchBarField
import com.notes.ui.StyledChip

@Composable
fun NotesListUI(
    modifier: Modifier = Modifier,
    addAction: () -> Unit,
    onSettingsClick: () -> Unit = {},
    onSelected: (Notes) -> Unit,
    notes: NotesCollection,
    onBackClick: () -> Unit = {},
    isPhoneSize: Boolean
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SearchBarField(
                trailingIcon = {
                    // Show settings conditionally for phone devices
                    if (isPhoneSize) {
                        IconButton(
                            onClick = { onSettingsClick() },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                },
                onBackClick = onBackClick,
            )
        },
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
            modifier = Modifier
                .padding(innerPadding)
                .testTag("notes_list"),
            notes = notes,
            onSelected = onSelected,
            isPhoneSize = isPhoneSize
        )
    }
}

@Composable
fun NotesList(
    modifier: Modifier = Modifier,
    notes: NotesCollection,
    onSelected: (Notes) -> Unit,
    isPhoneSize: Boolean
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
                }
            )
        }
    } else {
        if (isPhoneSize) {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
            ) {
                for (note in notes.collection) {
                    item(key = note.id) {
                        EditorPreviewStateful(
                            content = note.content,
                            id = note.id,
                        ) { onSelected(note) }
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
                        EditorPreviewStateful(
                            content = note.content,
                            id = note.id,
                        ) { onSelected(note) }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditorPreviewStateful(
    content: String,
    id: Long,
    onClicked: () -> Unit,
) {

    var title by rememberSaveable {
        mutableStateOf("")
    }

    // Do not use rememberRichTextState as it dramatically slows down the list
    val state = remember {
        RichTextState()
    }

    LaunchedEffect(content) {
        state.clear()
        state.setHtml(content)
        // Get first line as a title
        val textContent = state.toText()
        val firstLine = textContent.substringBefore('\n')
        title = if (Platform().logger.isDebug) "[id=${id}] $firstLine"
        else firstLine
    }

    EditorPreview(state = state, title = title) {
        onClicked()
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

    Box(
        modifier = contentModifier,
    ) {

        val padding = Modifier.padding(4.dp)

        // Readonly field doesn't react on click events
        RichTextEditor(
            state = state,
            shape = RoundedCornerShape(10),
            readOnly = true,
            colors =
                richTextEditorColors(
                    // Remove bottom thin line
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
            modifier = contentModifier.then(padding),
        )

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
