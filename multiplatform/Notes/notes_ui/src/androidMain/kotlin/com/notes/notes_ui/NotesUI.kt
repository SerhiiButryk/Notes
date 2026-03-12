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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import api.data.Attachments
import api.data.Document.Companion.isUserFile
import api.data.Notes
import api.data.NotesCollection
import api.data.UserFile
import coil3.compose.AsyncImage
import com.notes.notes_ui.components.NotesNavRail
import com.notes.notes_ui.models.Tools
import com.notes.notes_ui.components.ViewModelCommand
import com.notes.notes_ui.editor.createEditorState
import com.notes.ui.SearchBarField
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
    getEvents: suspend () -> Flow<ViewModelCommand>,
    onSettingsClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    showNavRail: Boolean,
    isPhoneSize: Boolean,
    attachments: Attachments,
    onOpenPreview: (UserFile) -> Unit,
    onDelete: (UserFile) -> Unit,
    onAttachFile: () -> Unit,
) {
    NotesUIImpl(
        notes = notes,
        toolsPaneItems = toolsPaneItems,
        onAddAction = onAddAction,
        note = note,
        onSelectAction = onSelectAction,
        onNavigatedBack = onNavigatedBack,
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
    getEvents: suspend () -> Flow<ViewModelCommand>,
    onSettingsClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    showNavRail: Boolean,
    isPhoneSize: Boolean,
    attachments: Attachments,
    onOpenPreview: (UserFile) -> Unit,
    onDelete: (UserFile) -> Unit,
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
    getEvents: suspend () -> Flow<ViewModelCommand>,
    onSettingsClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    isPhoneSize: Boolean,
    attachments: Attachments,
    onOpenPreview: (UserFile) -> Unit,
    onDelete: (UserFile) -> Unit,
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

    var state by remember(note.content) {
        mutableStateOf(createEditorState(note))
    }

    NavigableListDetailPaneScaffold(
        navigator = navigator,
        listPane = {
            AnimatedPane {
                // Note List screen
                NotesListUI(
                    modifier =
                        Modifier.semantics {
                            testTagsAsResourceId = true
                        },
                    notes = notes,
                    onSelected = { selectedNote ->
                        // Open Note Editor Screen
                        coroutineScope.launch {
                            state = createEditorState(selectedNote)
                            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, null)
                            onSelectAction(selectedNote)
                        }
                    },
                    addAction = {
                        // Open Note Editor Screen
                        coroutineScope.launch {
                            state = createEditorState(Notes())
                            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, null)
                            onAddAction()
                        }
                    },
                    isPhoneSize = isPhoneSize,
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
                    }
                )
            }
        },
        detailPane = {
            AnimatedPane {
                LaunchedEffect(note) {
                    getEvents().collect { event ->
                        when (event) {
                            // Close editor
                            is ViewModelCommand.NavigateToListPane -> {
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

                val bottomSheetState =
                    rememberModalBottomSheetState(
                        skipPartiallyExpanded = false,
                    )

                val showFolderButton = attachments.hasAttachmentsFor(note.id)
                SideEffect {
                    if (bottomSheetState.isVisible && !showFolderButton) {
                        scope.launch { bottomSheetState.hide() }
                    }
                }

                NotesEditorUI(
                    notes = note,
                    state = state,
                    tools = toolsPaneItems,
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
                        onDelete,
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
    onClick: (UserFile) -> Unit,
    onDelete: (UserFile) -> Unit,
) {
    if (attachments.files.isNotEmpty()) {
        LazyRow {
            for (file in attachments.files) {
                if (isUserFile(file.file.name, notes.id)) {
                    item(file.file) {
                        Box(
                            modifier =
                                Modifier
                                    .width(250.dp)
                                    .height(250.dp)
                                    .clickable {
                                        onClick(file)
                                    },
                        ) {
                            AsyncImage(
                                model = Uri.fromFile(file.file),
                                contentDescription = "",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Inside,
                            )

                            IconButton(
                                onClick = { onDelete(file) },
                                modifier = Modifier.align(Alignment.TopEnd),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "",
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
