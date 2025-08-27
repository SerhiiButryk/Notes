package com.notes.notes_ui

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
import com.notes.notes_ui.NotesViewModel.Notes.Companion.EmptyNote
import com.notes.ui.isTabletOrFoldableExpanded
import com.notes.ui.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
fun NotesUI(
    modifier: Modifier = Modifier,
    notes: List<NotesViewModel.Notes>
) {
    AppTheme {
        NotesUIImpl(notes = notes)
    }
}

@Composable
private fun NotesUIImpl(notes: List<NotesViewModel.Notes>) {

    Row {
        // TODO: Might need to pass this from outside. Recalculation every time might be slow.
        val sizeClass = currentWindowAdaptiveInfo().windowSizeClass

        // Show nav rail for large screens
        if (isTabletOrFoldableExpanded(sizeClass)) {
            NotesNavRail()
        }

        ListDetailUI(notes = notes, sizeClass = sizeClass)
    }

}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun ListDetailUI(
    notes: List<NotesViewModel.Notes>,
    sizeClass: WindowSizeClass
) {

    val defaultDirective = rememberListDetailPaneScaffoldNavigator().scaffoldDirective

    // TODO: Dig deeper into this APIs
    val customDirective = remember() {
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
                        // Open to Note Editor Screen
                        coroutineScope.launch {
                            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, note)
                        }
                    },
                    sizeClass = sizeClass,
                    addAction = {
                        // Open to Note Editor Screen
                        coroutineScope.launch {
                            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, EmptyNote())
                        }
                    }
                )
            }
        },
        detailPane = {
            AnimatedPane {
                val note: NotesViewModel.Notes? = navigator.currentDestination?.contentKey

                val state = rememberRichTextState()

                // Set initial content
                LaunchedEffect(note) {
                    state.setText(note?.content ?: "")
                }

                // Note editor screen
                NotesEditorUI(notes = note, state = state)
            }
        }
    )
}