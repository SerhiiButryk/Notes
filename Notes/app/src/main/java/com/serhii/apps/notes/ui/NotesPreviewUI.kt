/*
 * Copyright 2024. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.serhii.apps.notes.R
import com.serhii.apps.notes.ui.state_holders.NotesViewModel

/**
 * Notes preview screen
 */

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NotesPreviewUI(uiState: NotesViewModel.NotesMainUIState, viewModel: NotesViewModel, menuOptionsList: List<MenuOptions>) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.openNoteEditorUI() }) {
                Icon(Icons.Default.Add, contentDescription = "")
            }
        }
    ) { _ ->

        val noNotesYet = uiState.notes.isEmpty()

        Column {
            SearchUI(hint = "Search notes here", menuOptionsList = menuOptionsList)
            if (noNotesYet) {
                NoNotesYetUI()
            } else {
                NotesUI(uiState)
            }
        }
    }
}

@Composable
private fun NoNotesYetUI() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.no_notes_icon),
                modifier = Modifier.size(width = 60.dp, height = 60.dp),
                contentDescription = ""
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            DescriptionUI(stringResource(id = R.string.ms_no_notes))
        }
    }
}

@Composable
private fun NotesUI(uiState: NotesViewModel.NotesMainUIState) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(120.dp),
        modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp)
    ) {
        items(10) {
            Card(
                modifier = Modifier
                    .padding(all = 4.dp)
                    .defaultMinSize(minHeight = 100.dp)
                    .clip(CardDefaults.shape),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                DescriptionUI(desc = "Test title")
                DescriptionUI(desc = "Test decs")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotesUIPreview() {
    NotesUI(NotesViewModel.NotesMainUIState())
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun NotesUIPreviewDark() {
    NotesUI(NotesViewModel.NotesMainUIState())
}