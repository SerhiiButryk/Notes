package com.notes.auth_ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SurfaceContainer(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 8.dp,
        modifier = Modifier.padding(start = 10.dp, end = 10.dp)
    ) {
        content()
    }
}

@Composable
fun HeaderTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 24.sp,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier
            .padding(bottom = 40.dp, top = 20.dp, start = 10.dp)
    )
}