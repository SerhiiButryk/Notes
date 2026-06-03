package com.notes.notes_ui

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import api.data.Attachments
import api.data.Image
import api.data.Notes
import api.data.NotesCollection
import coil3.compose.AsyncImage
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.notes.notes_ui.components.NotesNavRail
import com.notes.notes_ui.data.Tools
import com.notes.notes_ui.data.UiEvent
import com.notes.notes_ui.editor.EditorCommand
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun NotesUI(
    modifier: Modifier = Modifier,
    notes: NotesCollection,
    toolsPaneItems: Tools,
    note: Notes,
    onAddAction: suspend () -> Unit,
    onSelectAction: suspend (Notes) -> Unit,
    onNavigatedBack: suspend () -> Unit,
    onTextChanged: (EditorCommand) -> Unit,
    getEvents: suspend () -> Flow<UiEvent>,
    onSettingsClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    showNavRail: Boolean,
    isPhoneSize: Boolean,
    attachments: Attachments,
    onOpenPreview: (Image) -> Unit,
    onDelete: (Image) -> Unit,
    onAttachFile: () -> Unit,
) {
    NotesUIImpl(
        notes = notes,
        toolsPaneItems = toolsPaneItems,
        onAddAction = onAddAction,
        note = note,
        onSelectAction = onSelectAction,
        onNavigatedBack = onNavigatedBack,
        onTextChanged = onTextChanged,
        getEvents = getEvents,
        onSettingsClick = onSettingsClick,
        onBackClick = onBackClick,
        showNavRail = showNavRail,
        isPhoneSize = isPhoneSize,
        attachments = attachments,
        onOpenPreview = onOpenPreview,
        onDelete = onDelete,
        onAttachFile = onAttachFile,
    )
}

@Composable
private fun NotesUIImpl(
    notes: NotesCollection,
    toolsPaneItems: Tools,
    note: Notes,
    onAddAction: suspend () -> Unit,
    onSelectAction: suspend (Notes) -> Unit,
    onNavigatedBack: suspend () -> Unit,
    onTextChanged: (EditorCommand) -> Unit,
    getEvents: suspend () -> Flow<UiEvent>,
    onSettingsClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    showNavRail: Boolean,
    isPhoneSize: Boolean,
    attachments: Attachments,
    onOpenPreview: (Image) -> Unit,
    onDelete: (Image) -> Unit,
    onAttachFile: () -> Unit,
) {
    Row {

        // Show nav rail for large screens
        if (showNavRail) {
            NotesNavRail(onSettingsClick = onSettingsClick)
        }

        ListDetailUI(
            notes = notes,
            toolsPaneItems = toolsPaneItems,
            onAddAction = onAddAction,
            note = note,
            onSelectAction = onSelectAction,
            onNavigatedBack = onNavigatedBack,
            onTextChanged = onTextChanged,
            getEvents = getEvents,
            onSettingsClick = onSettingsClick,
            onBackClick = onBackClick,
            isPhoneSize = isPhoneSize,
            attachments = attachments,
            onOpenPreview = onOpenPreview,
            onDelete = onDelete,
            onAttachFile = onAttachFile,
        )
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ListDetailUI(
    notes: NotesCollection,
    toolsPaneItems: Tools,
    note: Notes,
    onAddAction: suspend () -> Unit,
    onSelectAction: suspend (Notes) -> Unit,
    onNavigatedBack: suspend () -> Unit,
    onTextChanged: (EditorCommand) -> Unit,
    getEvents: suspend () -> Flow<UiEvent>,
    onSettingsClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    isPhoneSize: Boolean,
    attachments: Attachments,
    onOpenPreview: (Image) -> Unit,
    onDelete: (Image) -> Unit,
    onAttachFile: () -> Unit,
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
                    modifier = Modifier.semantics {
                        testTagsAsResourceId = true
                    },
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
                    addAction = {
                        // Open Note Editor Screen
                        coroutineScope.launch {
                            state.clear() // Clear editor before opening
                            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, null)
                            onAddAction()
                        }
                    },
                    onSettingsClick = onSettingsClick,
                    onBackClick = onBackClick,
                    isPhoneSize = isPhoneSize
                )
            }
        },
        detailPane = {
            AnimatedPane {

                LaunchedEffect(note) {
                    getEvents().collect { event ->
                        when (event) {
                            // Close editor
                            is UiEvent.NavigateToListPane -> {
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

                val scope = rememberCoroutineScope()

                val bottomSheetState = rememberModalBottomSheetState(
                    skipPartiallyExpanded = false
                )

                val showFolderButton = attachments.hasAttachmentsFor(note.id)
                SideEffect {
                    if (bottomSheetState.isVisible && !showFolderButton) {
                        scope.launch {  bottomSheetState.hide() }
                    }
                }

                NotesEditorUI(
                    notes = note,
                    state = state,
                    toolsPaneItems = toolsPaneItems,
                    onTextChanged = onTextChanged,
                    onAttacheFile = onAttachFile,
                    showFolderButton = showFolderButton,
                    bottomSheetState = bottomSheetState,
                ) {
                    MediaPreview(
                        attachments,
                        note,
                        {
                            scope.launch {
                                bottomSheetState.hide()
                                onOpenPreview(it)
                            }
                        },
                        onDelete
                    )
                }

            }
        },
    )
}

@Composable
fun MediaPreview(
    attachments: Attachments,
    notes: Notes,
    onClick: (Image) -> Unit,
    onDelete: (Image) -> Unit,
) {
    if (attachments.images.isNotEmpty()) {
        LazyRow {
            for (image in attachments.images) {
                // Image name is "1_img_09"
                // '1' - id of note which it belongs to
                // So, we add only images which actually attached to this note
                if (image.name.startsWith(notes.id.toString())) {
                    item(image.location) {
                        Box(
                            modifier = Modifier
                                .width(250.dp)
                                .height(250.dp)
                                .clickable {
                                    onClick(image)
                                },
                        ) {

                            AsyncImage(
                                model = image.location as Uri,
                                contentDescription = "",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Inside
                            )

                            IconButton(
                                onClick = { onDelete(image) },
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = ""
                                )
                            }

                        }
                    }
                }
            }
        }
    }
}
