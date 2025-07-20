package com.notes.notes_ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notes.ui.theme.AppTheme

@Composable
fun NotesUI(modifier: Modifier = Modifier) {
    AppTheme {
        NotesUIImpl()
    }
}

@Composable
private fun NotesUIImpl() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            SearchBarAdaptive()
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
            ) {
                Icon(Icons.Filled.Add, "Floating action button.")
            }
        }
    ) { innerPadding ->

        val modifier = Modifier.padding(top = innerPadding.calculateTopPadding(), bottom = innerPadding.calculateBottomPadding())

        NotesList(modifier = modifier)
    }
}

@Composable
private fun NotesList(modifier: Modifier = Modifier) {
    LazyVerticalStaggeredGrid(
        modifier = modifier,
        columns = StaggeredGridCells.Adaptive(160.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        item {
            CardNote()
        }

        item {
            CardNote()
        }

        item {
            CardNote()
        }

        item {
            CardNote()
        }

        item {
            CardNote()
        }

        item {
            CardNote()
        }

        item {
            CardNote()
        }
    }
}

@Composable
private fun CardNote() {
    Box(
        modifier = Modifier
            .heightIn(min = 40.dp)
            .combinedClickable(
                onLongClick = { },
                onClick = { }
            )
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = "Hello"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBarAdaptive(modifier: Modifier = Modifier) {
    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                expanded = false,
                query = "",
                onQueryChange = {  },
                onSearch = {  }, // Collapse on search submission
                onExpandedChange = {  },
                placeholder = { Text("Search your notes...") },
                leadingIcon = {
                    if (true) {
                        IconButton(onClick = {
//                            active = false
//                            query = "" // Clear query when collapsing with back button
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    } else {
                        Icon(Icons.Default.Search, contentDescription = "Search icon")
                    }
                },
                trailingIcon = {
                    if (/*active && query.isNotEmpty()*/false) {
                        IconButton(onClick = { /*query = ""*/ }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search")
                        }
                    }
                },
            )
        },
        expanded = false,
        onExpandedChange = {},
        modifier = Modifier.fillMaxWidth().padding(start = 4.dp, end = 4.dp, bottom = 6.dp),
    ) {

    }
}

@Preview
@Preview(
    uiMode = UI_MODE_TYPE_NORMAL or UI_MODE_NIGHT_YES,
    device = "spec:parent=pixel_5,orientation=landscape"
)
@Composable
private fun NotesUIPrev() {
    NotesUIImpl()
}
