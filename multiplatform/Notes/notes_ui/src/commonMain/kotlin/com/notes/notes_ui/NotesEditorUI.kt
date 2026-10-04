package com.notes.notes_ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import api.data.AppSettings
import api.data.Notes
import com.notes.notes_ui.components.ToolsBar
import com.notes.notes_ui.models.Tools
import com.notes.ui.theme.backgroundColor
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
    showTopBar: Boolean = true,
    withAnimation: Boolean = false,
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
        showTopBar,
        withAnimation,
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
    showTopBar: Boolean,
    withAnimation: Boolean,
) {
    // Controller to hide the keyboard when Boot Sheet is going to be shown.
    // In such case we will have smooth UI transition to new state
    val keyboardController = LocalSoftwareKeyboardController.current

    var showFolderContent by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(false) {
        if (showFolderButton) {
            keyboardController?.hide()
        }
    }

    if (notes == Notes.AbsentNote()) {
        InfoLabel()
    } else {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(color = backgroundColor())
                    .navigationBarsPadding()
                    .then(modifier)
        ) {

            if (withAnimation) {
                // Add Cross-fade animation
                Crossfade(
                    targetState = notes,
                    label = "Editor cross fade animation",
                ) { note ->
                    EditorMainContent(
                        notes = note,
                        onAttacheFile = onAttacheFile,
                        onShowFolder = {
                            keyboardController?.hide()
                            showFolderContent = true
                        },
                        showFolderButton = showFolderButton,
                        state = state,
                        showTopBar = showTopBar,
                        tools = tools,
                    )
                }
            } else {
                EditorMainContent(
                    notes = notes,
                    onAttacheFile = onAttacheFile,
                    onShowFolder = {
                        keyboardController?.hide()
                        showFolderContent = true
                    },
                    showFolderButton = showFolderButton,
                    state = state,
                    showTopBar = showTopBar,
                    tools = tools,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnScope.EditorMainContent(
    notes: Notes,
    showTopBar: Boolean,
    showFolderButton: Boolean,
    onShowFolder: () -> Unit,
    onAttacheFile: () -> Unit,
    state: RichTextState,
    tools: Tools,
) {
    if (notes != Notes.AbsentNote() && showTopBar) {
        TopAppBar(
            modifier = Modifier
                .height(90.dp),
            title = { },
            actions = {
                Row(
                    modifier = Modifier
                        .height(90.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (showFolderButton) {
                        IconButton(onClick = onShowFolder) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = "",
                            )
                        }
                    }
                    if (AppSettings.attachmentsEnabled) {
                        IconButton(onClick = onAttacheFile) {
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
                    containerColor = backgroundColor(),
                    titleContentColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
        )
    }

    // If we don't show top bar then don't show toolbar as well
    if (showTopBar) {
        ToolsBar(
            state = state,
            tools = tools,
            notes = notes,
        )
    }

    Column(
        modifier = Modifier
            .padding(6.dp)
            .weight(1f)
            .verticalScroll(rememberScrollState())
    ) {
        EditorLayout(
            state = state,
        )
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
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .then(modifier),
        textStyle =
            MaterialTheme.typography.bodyLarge.copy(
                fontSize = fontSize,
                color = MaterialTheme.colorScheme.onSurface,
            ),
        styleResolver = rememberMaterial3AttributeStyleResolver(),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
        keyboardOptions = KeyboardOptions(
            autoCorrectEnabled = false,
            keyboardType = KeyboardType.Text
        ),
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
