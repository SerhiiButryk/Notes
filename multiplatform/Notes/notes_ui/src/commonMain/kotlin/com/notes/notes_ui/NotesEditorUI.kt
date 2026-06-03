package com.notes.notes_ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import api.data.Notes
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults.richTextEditorColors
import com.notes.notes_ui.components.ToolsBar
import com.notes.notes_ui.data.Tools
import com.notes.notes_ui.editor.EditorCommand
import com.notes.notes_ui.editor.TextInputCommand
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesEditorUI(
    modifier: Modifier = Modifier,
    notes: Notes,
    state: RichTextState,
    toolsPaneItems: Tools,
    onTextChanged: (EditorCommand) -> Unit,
    onAttacheFile: () -> Unit = {},
    showFolderButton: Boolean,
    bottomSheetState: SheetState,
    content: @Composable () -> Unit = {},
) {
    EditorUI(
        modifier,
        notes,
        state,
        toolsPaneItems,
        onTextChanged,
        onAttacheFile,
        content,
        showFolderButton,
        bottomSheetState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorUI(
    modifier: Modifier = Modifier,
    notes: Notes,
    state: RichTextState,
    toolsPaneItems: Tools,
    onTextChanged: (EditorCommand) -> Unit,
    onAttacheFile: () -> Unit,
    content: @Composable () -> Unit,
    showFolderButton: Boolean,
    bottomSheetState: SheetState,
) {

    var showFolderContent by rememberSaveable { mutableStateOf(showFolderButton) }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(bottom = 4.dp),
                title = { },
                actions = {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (showFolderButton) {
                            IconButton(onClick = {
                                showFolderContent = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Folder,
                                    contentDescription = ""
                                )
                            }
                        }
                        IconButton(onClick = { onAttacheFile() }) {
                            Icon(
                                imageVector = Icons.Default.AttachFile,
                                contentDescription = ""
                            )
                        }
                    }

                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.surfaceContainer,
                )
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->

        // TODO:
        // Crossfade() animation adds flickering ui issues
        // and it doen't look good. Disabled for now.

        // Adds cross fade animation when selecting a note from the list
        /*Crossfade(
            targetState = notes,
            label = "Editor cross fade animation",
            modifier =
                Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .imePadding(),
        ) { note -> } */

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .imePadding(),
        ) {

            if (notes == Notes.AbsentNote()) {
                InfoLabel()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()

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

        if (showFolderContent) {
            ModalBottomSheet(
                onDismissRequest = {
                    showFolderContent = false
                },
                sheetState = bottomSheetState,
                dragHandle = {
                    BottomSheetDefaults.DragHandle()
                }
            ) {
                content()
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
    val lastHtml = remember { mutableStateOf(state.toHtml()) }

    RichTextEditor(
        state = state,
        modifier =
            Modifier
                .fillMaxSize()
                .then(modifier),
        colors =
            richTextEditorColors(
                // Remove bottom thin line
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
        shape = RoundedCornerShape(4),
        onTextChanged = { newHtml ->
            val oldHtml = lastHtml.value
            lastHtml.value = newHtml
            coroutineScope.launch {
                val command = TextInputCommand(newHtml, oldHtml, state)
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
