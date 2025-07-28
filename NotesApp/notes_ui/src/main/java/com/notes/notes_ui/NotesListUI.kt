package com.notes.notes_ui

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NotesList(modifier: Modifier = Modifier, showList: Boolean) {

    if (showList) {

        LazyColumn(
            modifier = modifier
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

    } else {

        LazyVerticalStaggeredGrid(
            modifier = modifier,
            columns = StaggeredGridCells.Adaptive(160.dp)
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
}

@Composable
private fun CardNote() {
    Box(
        modifier = Modifier
            .heightIn(min = 40.dp)
            .combinedClickable(onLongClick = { }, onClick = { })) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            Text(
                modifier = Modifier.padding(8.dp), text = "Hello"
            )
        }
    }
}