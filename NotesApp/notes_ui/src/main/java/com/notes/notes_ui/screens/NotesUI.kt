package com.notes.notes_ui.screens

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.notes.notes_ui.NotesViewModel
import com.notes.notes_ui.NotesViewModel.Actions
import com.notes.notes_ui.NotesViewModel.Notes
import com.notes.notes_ui.screens.components.NotesListUI
import com.notes.notes_ui.screens.components.NotesNavRail
import com.notes.notes_ui.screens.editor.ToolsPane
import com.notes.ui.isTabletOrFoldableExpanded
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun NotesUI(
    modifier: Modifier = Modifier,
    notes: List<Notes>,
    toolsPaneItems: List<ToolsPane>,
    getNote: () -> StateFlow<Notes>,
    onAddAction: suspend () -> Unit,
    onSelectAction: suspend (Notes) -> Unit,
    getActions: () -> SharedFlow<Actions>
) {
    NotesUIImpl(
        notes = notes,
        toolsPaneItems = toolsPaneItems,
        onAddAction = onAddAction,
        getNote = getNote,
        onSelectAction = onSelectAction,
        getActions = getActions
    )
}

@Composable
private fun NotesUIImpl(
    notes: List<Notes>,
    toolsPaneItems: List<ToolsPane>,
    getNote: () -> StateFlow<Notes>,
    onAddAction: suspend () -> Unit,
    onSelectAction: suspend (Notes) -> Unit,
    getActions: () -> SharedFlow<Actions>
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
            getNote = getNote,
            onSelectAction = onSelectAction,
            getActions = getActions
        )
    }

}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun ListDetailUI(
    notes: List<Notes>,
    sizeClass: WindowSizeClass,
    toolsPaneItems: List<ToolsPane>,
    getNote: () -> StateFlow<Notes>,
    onAddAction: suspend () -> Unit,
    onSelectAction: suspend (Notes) -> Unit,
    getActions: () -> SharedFlow<Actions>
) {

    val defaultDirective = rememberListDetailPaneScaffoldNavigator().scaffoldDirective

    // TODO: Dig deeper into this APIs
    val customDirective = remember {
        PaneScaffoldDirective(
            // Applied workaround to remove a horizontal space between 2 panes
            // which more likely is added to handle hinges
            horizontalPartitionSpacerSize = 0.dp,
            maxHorizontalPartitions = defaultDirective.maxHorizontalPartitions,
            maxVerticalPartitions = defaultDirective.maxVerticalPartitions,
            verticalPartitionSpacerSize = defaultDirective.verticalPartitionSpacerSize,
            defaultPanePreferredWidth = defaultDirective.defaultPanePreferredWidth,
            excludedBounds = defaultDirective.excludedBounds
        )
    }

    val navigator = rememberListDetailPaneScaffoldNavigator<NotesViewModel.Notes>(
        scaffoldDirective = customDirective
    )

    val coroutineScope = rememberCoroutineScope()

    NavigableListDetailPaneScaffold(
        navigator = navigator,
        listPane = {
            AnimatedPane {
                // Note List screen
                NotesListUI(
                    notes = notes,
                    onSelected = { note ->
                        // Open Note Editor Screen
                        coroutineScope.launch {
                            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, null)
                            onSelectAction(note)
                        }
                    },
                    sizeClass = sizeClass,
                    addAction = {
                        // Open Note Editor Screen
                        coroutineScope.launch {
                            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, null)
                            onAddAction()
                        }
                    }
                )
            }
        },
        detailPane = {
            AnimatedPane {

                val state = rememberRichTextState()
                val noteState = getNote().collectAsStateWithLifecycle()

                LaunchedEffect(noteState.value) {
                    // Clear previous styles and states
                    state.clear()
                    state.setHtml(noteState.value.content)

                    getActions().collect {
                        if (it is Actions.NavBackAction) {
                            navigator.navigateBack()
                        }
                    }
                }

                NotesEditorUI(notes = noteState.value, state = state, toolsPaneItems = toolsPaneItems)
            }
        }
    )
}