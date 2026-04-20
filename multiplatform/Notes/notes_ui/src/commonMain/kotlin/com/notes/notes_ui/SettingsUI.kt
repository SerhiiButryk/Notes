package com.notes.notes_ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.notes.ui.SimpleTopBar

@Composable
fun SettingsUI(
    onBackClick: () -> Unit,
    onAccountClick: () -> Unit,
    onExportClick: () -> Unit,
    onPasswordUpdateClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            SimpleTopBar(title = "Settings", onBackClick = onBackClick)
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(vertical = 8.dp)
        ) {

            SettingsListItem(
                title = "Account",
                subtitle = "Profile, privacy, and security",
                icon = Icons.Default.Person,
                onClick = onAccountClick,
                showRightSign = true
            )

            SettingsListItem(
                title = "Export to a PDF file",
                subtitle = "Save your notes to a pdf file",
                icon = Icons.Default.Backup,
                onClick = onExportClick,
                showRightSign = false
            )

            SettingsListItem(
                title = "Change a password",
                subtitle = "Update your password",
                icon = Icons.Default.Key,
                onClick = onPasswordUpdateClick,
                showRightSign = false
            )

        }
    }
}

@Composable
fun SettingsListItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    showRightSign: Boolean,
) {
    Surface(
        onClick = onClick, color = Color.Transparent // Keeps the Ripple effect visible
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (showRightSign) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}