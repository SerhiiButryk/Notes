package com.notes.notes_ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.notes.ui.theme.AppTheme

@Composable
fun NotesUI(modifier: Modifier = Modifier) {
    AppTheme {
        NotesUIImpl()
    }
}

@Composable
fun NotesUIImpl() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Text(modifier = Modifier.padding(innerPadding),
        text = "Notes content !!!")
    }
}
