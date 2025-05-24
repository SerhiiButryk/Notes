package com.notes.auth_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.notes.ui.theme.AppTheme
import com.notes.ui.theme.Purple80

@Composable
fun LoginUI() {
    AppTheme {
        LoginUIImpl()
    }
}

@Composable
fun LoginUIImpl() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .background(Purple80),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Hello world !")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginUIPreview() {
    AppTheme {
        LoginUIImpl()
    }
}