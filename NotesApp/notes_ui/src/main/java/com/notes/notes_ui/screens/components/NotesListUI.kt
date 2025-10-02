package com.notes.notes_ui.screens.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.notes.notes_ui.NotesViewModel
import com.notes.ui.SearchBarField
import com.notes.ui.isTabletOrFoldableExpanded

@Composable
fun NotesListUI(
    modifier: Modifier = Modifier,
    addAction: () -> Unit,
    onSettingsAction: () -> Unit = {},
    onSelected: (NotesViewModel.Notes) -> Unit,
    notes: List<NotesViewModel.Notes>,
    sizeClass: WindowSizeClass,
) {
    Scaffold(modifier = modifier.fillMaxSize(), topBar = {
        SearchBarField(
            trailingIcon = {
                // Show settings conditionally for phone devices
                if (!isTabletOrFoldableExpanded(sizeClass)) {
                    IconButton(
                        onClick = { onSettingsAction() }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            })
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = { addAction() },
        ) {
            Icon(Icons.Filled.Add, null)
        }
    }) { innerPadding ->
        NotesList(
            modifier = Modifier.padding(innerPadding),
            sizeClass = sizeClass,
            notes = notes,
            onSelected = onSelected
        )
    }
}

@Composable
fun NotesList(
    modifier: Modifier = Modifier,
    notes: List<NotesViewModel.Notes>,
    onSelected: (NotesViewModel.Notes) -> Unit,
    sizeClass: WindowSizeClass
) {
    if (notes.isEmpty()) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Create your first note by tapping '+' button")
        }
    } else {
        if (!isTabletOrFoldableExpanded(sizeClass)) {
            LazyColumn(
                modifier = modifier.fillMaxSize()
            ) {
                for (note in notes) {
                    item(key = note.id) {
                        CardNote(note = note, onSelected = onSelected)
                    }
                }
            }
        } else {
            LazyVerticalStaggeredGrid(
                modifier = modifier,
                columns = StaggeredGridCells.Adaptive(160.dp)
            ) {
                for (note in notes) {
                    item(key = note.id) {
                        CardNote(note = note, onSelected = onSelected)
                    }
                }
            }
        }
    }

}

@Composable
private fun CardNote(note: NotesViewModel.Notes, onSelected: (NotesViewModel.Notes) -> Unit) {
    Box(
        modifier = Modifier
            .heightIn(min = 40.dp)
            .combinedClickable(onLongClick = { }, onClick = { onSelected(note) })
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            Text(
                modifier = Modifier.padding(8.dp), text = note.content
            )
        }
    }
}