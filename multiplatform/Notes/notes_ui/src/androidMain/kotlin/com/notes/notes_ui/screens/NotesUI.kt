package com.notes.notes_ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import api.data.Notes
import com.notes.notes_ui.EditorCommand
import com.notes.notes_ui.NotesViewModel
import com.notes.notes_ui.screens.components.NotesListUI
import com.notes.notes_ui.screens.components.NotesNavRail
import com.notes.notes_ui.screens.editor.ToolsPane
import com.notes.ui.isTabletOrFoldableExpanded
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun NotesUI(
    modifier: Modifier = Modifier,
    notes: List<Notes>,
    toolsPaneItems: List<ToolsPane>,
    note: Notes,
    onAddAction: suspend () -> Unit,
    onSelectAction: suspend (Notes) -> Unit,
    onNavigatedBack: suspend () -> Unit,
    onTextChanged: (EditorCommand) -> Unit,
    getEvents: suspend () -> Flow<NotesViewModel.UiEvent>
) {
    NotesUIImpl(
        notes = notes,
        toolsPaneItems = toolsPaneItems,
        onAddAction = onAddAction,
        note = note,
        onSelectAction = onSelectAction,
        onNavigatedBack = onNavigatedBack,
        onTextChanged = onTextChanged,
        getEvents = getEvents
    )
}

@Composable
private fun NotesUIImpl(
    notes: List<Notes>,
    toolsPaneItems: List<ToolsPane>,
    note: Notes,
    onAddAction: suspend () -> Unit,
    onSelectAction: suspend (Notes) -> Unit,
    onNavigatedBack: suspend () -> Unit,
    onTextChanged: (EditorCommand) -> Unit,
    getEvents: suspend () -> Flow<NotesViewModel.UiEvent>
) {
    Row {
        // TODO: Might need to pass this from outside. Recalculation every time might be slow.
        val sizeClass = currentWindowAdaptiveInfo().windowSizeClass

        // Show nav rail for large screens
        if (isTabletOrFoldableExpanded(sizeClass)) {
            NotesNavRail()
        }

        ListDetailUI(
            notes = notes,
            sizeClass = sizeClass,
            toolsPaneItems = toolsPaneItems,
            onAddAction = onAddAction,
            note = note,
            onSelectAction = onSelectAction,
            onNavigatedBack = onNavigatedBack,
            onTextChanged = onTextChanged,
            getEvents = getEvents
        )
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun ListDetailUI(
    notes: List<Notes>,
    sizeClass: WindowSizeClass,
    toolsPaneItems: List<ToolsPane>,
    note: Notes,
    onAddAction: suspend () -> Unit,
    onSelectAction: suspend (Notes) -> Unit,
    onNavigatedBack: suspend () -> Unit,
    onTextChanged: (EditorCommand) -> Unit,
    getEvents: suspend () -> Flow<NotesViewModel.UiEvent>
) {
    val defaultDirective = rememberListDetailPaneScaffoldNavigator().scaffoldDirective

    // TODO: Dig deeper into this APIs
    val customDirective =
        remember {
            PaneScaffoldDirective(
                // Applied workaround to remove a horizontal space between 2 panes
                // which more likely is added to handle hinges
                horizontalPartitionSpacerSize = 0.dp,
                maxHorizontalPartitions = defaultDirective.maxHorizontalPartitions,
                maxVerticalPartitions = defaultDirective.maxVerticalPartitions,
                verticalPartitionSpacerSize = defaultDirective.verticalPartitionSpacerSize,
                defaultPanePreferredWidth = defaultDirective.defaultPanePreferredWidth,
                excludedBounds = defaultDirective.excludedBounds,
            )
        }

    val navigator =
        rememberListDetailPaneScaffoldNavigator<Notes>(
            scaffoldDirective = customDirective,
        )

    val coroutineScope = rememberCoroutineScope()

    val state = rememberRichTextState()

    LaunchedEffect(false) {
        // Set text when the editor is open
        state.clear()
        state.setHtml(note.content)
    }

    NavigableListDetailPaneScaffold(
        navigator = navigator,
        listPane = {
            AnimatedPane {
                // Note List screen
                NotesListUI(
                    notes = notes,
                    onSelected = { selectedNote ->
                        // Open Note Editor Screen
                        coroutineScope.launch {
                            state.clear() // Clear editor before opening
                            state.setHtml(selectedNote.content) // Set content
                            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, null)
                            onSelectAction(selectedNote)
                        }
                    },
                    sizeClass = sizeClass,
                    addAction = {
                        // Open Note Editor Screen
                        coroutineScope.launch {
                            state.clear() // Clear editor before opening
                            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, null)
                            onAddAction()
                        }
                    },
                )
            }
        },
        detailPane = {
            AnimatedPane {

                LaunchedEffect(note) {
                    getEvents().collect { event ->
                        when (event) {
                            // Close editor
                            is NotesViewModel.UiEvent.NavigateToListPane -> {
                                try {
                                    navigator.navigateTo(ListDetailPaneScaffoldRole.List, null)
                                } finally {
                                    onNavigatedBack()
                                }
                            }
                        }
                    }
                }

                BackHandler(enabled = true) {
                    coroutineScope.launch {
                        try {
                            // Could throw cancellation exception
                            navigator.navigateBack()
                        } finally {
                            onNavigatedBack()
                        }
                    }
                }

                NotesEditorUI(
                    notes = note,
                    state = state,
                    toolsPaneItems = toolsPaneItems,
                    onTextChanged = onTextChanged,
                )
            }
        },
    )
}
