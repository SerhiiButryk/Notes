package com.notes.auth_ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notes.ui.theme.SurfaceColor

@Composable
fun SurfaceContainer(modifier: Modifier = Modifier, content: @Composable () -> Unit = {}) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 8.dp,
        modifier = Modifier.padding(all = 10.dp).then(modifier),
        color = SurfaceColor()
    ) {
        content()
    }
}

@Composable
fun Header(text: String, modifier: Modifier = Modifier, textAlign: TextAlign? = null) {
    Text(
        text = text,
        fontSize = 24.sp,
        style = MaterialTheme.typography.headlineSmall,
        modifier = modifier,
        textAlign = textAlign
    )
}

@Composable
fun SubHeader(text: String, modifier: Modifier = Modifier, textAlign: TextAlign = TextAlign.Start) {
    Text(
        text = text,
        fontSize = 18.sp,
        style = TextStyle(fontWeight = FontWeight.Bold, textAlign = textAlign),
        modifier = modifier
    )
}