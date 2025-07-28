package com.notes.notes_ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NotesNavRail(onShowSettings: () -> Unit = {}) {
    NavigationRail(
        header = {
//            TODO Set app icon
//            Icon(
//                imageVector = Icons.Filled.Settings,
//                contentDescription = null,
//                Modifier.padding(vertical = 12.dp)
//                tint = MaterialTheme.colorScheme.primary,
//            )
        }) {
        Spacer(Modifier.weight(1f))

        NavigationRailItem(
            selected = true,
            onClick = { onShowSettings() },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
            label = { Text("Settings") },
            alwaysShowLabel = false,
        )

        Spacer(Modifier.weight(1f))
    }
}
