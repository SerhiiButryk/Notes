package com.notes.notes_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import api.data.AppSettings
import api.data.Notes
import com.notes.notes_ui.components.ToolsBar
import com.notes.notes_ui.models.Tools
import dev.mkeeda.arranger.richtext.editor.RichTextEditor
import dev.mkeeda.arranger.richtext.editor.RichTextState
import dev.mkeeda.arranger.richtext.editor.material3.rememberMaterial3AttributeStyleResolver

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesEditorUI(
    modifier: Modifier = Modifier,
    notes: Notes,
    state: RichTextState,
    tools: Tools,
    onAttacheFile: () -> Unit = {},
    showFolderButton: Boolean,
    bottomSheetState: SheetState,
    content: @Composable () -> Unit = {},
) {
    EditorUI(
        modifier,
        notes,
        state,
        tools,
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
    tools: Tools,
    onAttacheFile: () -> Unit,
    content: @Composable () -> Unit,
    showFolderButton: Boolean,
    bottomSheetState: SheetState,
) {
    // Controller to hide the keyboard when Boot Sheet is going to be shown.
    // In such case we will have smooth UI transition to new state
    val keyboardController = LocalSoftwareKeyboardController.current

    var showFolderContent by rememberSaveable { mutableStateOf(showFolderButton) }

    LaunchedEffect(false) {
        if (showFolderButton) {
            keyboardController?.hide()
        }
    }

    Scaffold(
        topBar = {
            if (notes != Notes.AbsentNote()) {
                TopAppBar(
                    modifier = Modifier.padding(bottom = 4.dp),
                    title = { },
                    actions = {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (showFolderButton) {
                                IconButton(onClick = {
                                    keyboardController?.hide()
                                    showFolderContent = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Folder,
                                        contentDescription = "",
                                    )
                                }
                            }
                            if (AppSettings.attachmentsEnabled) {
                                IconButton(onClick = { onAttacheFile() }) {
                                    Icon(
                                        imageVector = Icons.Default.AttachFile,
                                        contentDescription = "",
                                    )
                                }
                            }
                        }
                    },
                    colors =
                        TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            titleContentColor = MaterialTheme.colorScheme.surfaceContainer,
                        ),
                )
            }
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
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .imePadding(),
        ) {
            if (notes == Notes.AbsentNote()) {
                InfoLabel()
            } else {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(color = MaterialTheme.colorScheme.surface),
                ) {
                    EditorLayout(
                        state = state,
                        // add weight modifier to the composable to ensure
                        // that the composable is measured after the other
                        // composable is measured specifically after the tools pane.
                        modifier = Modifier.weight(1f),
                    )
                    ToolsBar(
                        state = state,
                        tools = tools,
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
                },
            ) {
                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorLayout(
    state: RichTextState,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 18.sp,
    readOnly: Boolean = false,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(state.richString.text) {
        if (!readOnly) {
            focusRequester.requestFocus()
        }
    }

    RichTextEditor(
        state = state,
        readOnly = readOnly,
        modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp)
                .focusRequester(focusRequester)
                .then(modifier),
        textStyle =
            MaterialTheme.typography.bodyLarge.copy(
                fontSize = fontSize,
                color = MaterialTheme.colorScheme.onSurface,
            ),
        styleResolver = rememberMaterial3AttributeStyleResolver(),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
    )
}

@Composable
private fun InfoLabel(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize(),
    ) {
        SuggestionChip(
            onClick = {},
            label = {
                Text(
                    text = "Select an item",
                    fontSize = 18.sp,
                )
            },
        )
    }
}
