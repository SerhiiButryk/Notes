package com.notes.notes_ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FormatBold
import androidx.compose.material.icons.outlined.FormatItalic
import androidx.compose.material.icons.outlined.FormatUnderlined
import androidx.compose.material.icons.outlined.StrikethroughS
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.notes.ui.CLEAR_ALL
import com.notes.ui.SAVE_ICON

@Composable
fun ToolsPane(modifier: Modifier = Modifier) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {

        item {
            ToolButton(
                imageVector = SAVE_ICON
            ) { /* on click */ }
        }

        item {
            ToolButton(
                imageVector = CLEAR_ALL
            ) { /* on click */ }
        }

        item {
            ToolButton(
                id = R.drawable.format_h1
            ) { /* on click */ }
        }

        item {
            ToolButton(
                id = R.drawable.format_h2
            ) { /* on click */ }
        }

        item {
            ToolButton(
                id = R.drawable.format_h3
            ) { /* on click */ }
        }

        item {
            ToolButton(
                id = R.drawable.format_h4
            ) { /* on click */ }
        }

        item {
            ToolButton(
                id = R.drawable.format_h5
            ) { /* on click */ }
        }

        item {
            ToolButton(
                id = R.drawable.format_h6
            ) { /* on click */ }
        }

        item {
            ToolButton(
                imageVector = Icons.Outlined.FormatBold
            ) { /* on click */ }
        }

        item {
            ToolButton(
                imageVector = Icons.Outlined.FormatItalic
            ) { /* on click */ }
        }

        item {
            ToolButton(
                imageVector = Icons.Outlined.FormatUnderlined
            ) { /* on click */ }
        }

        item {
            ToolButton(
                imageVector = Icons.Outlined.StrikethroughS
            ) { /* on click */ }
        }

    }
}

@Composable
private fun ToolButton(
    imageVector: ImageVector? = null,
    @DrawableRes id: Int = 0,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        if (imageVector != null) {
            Icon(
                imageVector = imageVector,
                contentDescription = ""
            )
        } else {
            Icon(
                painter = painterResource(id = id),
                contentDescription = ""
            )
        }
    }
}