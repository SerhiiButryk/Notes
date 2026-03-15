package com.notes.notes_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.notes.notes_ui.data.AccountInfo
import com.notes.ui.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountUI(
    onBackClick: () -> Unit,
    onSignOut: () -> Unit,
    onGrantPermissionClick: () -> Unit,
    accountInfo: AccountInfo,
) {
    Scaffold(
        topBar = {
            SimpleTopBar(title = "Account Details", onBackClick = onBackClick)
        }) { innerPadding ->

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // --- Profile Section ---

            Box(
                modifier = Modifier.size(120.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Information List ---

            OutlinedTextField(
                value = accountInfo.email, // Bind to your state
                onValueChange = {},
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            val modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth()

            val status1 = if (accountInfo.googleIsActive) "Active" else "Not active"

            AccountStatusCard(
                title = "Google Account",
                status = status1,
                icon = getIconByKey(googleIcon),
                iconTint = Color.Unspecified,
                modifier = modifier,
                isOnline = accountInfo.googleIsActive,
            )

            val status2 = if (accountInfo.firebaseIsActive) "Active" else "Not active"

            AccountStatusCard(
                title = "Firebase Sync",
                status = status2,
                icon = getIconByKey(firebaseIcon),
                iconTint = Color(0xFFFFCA28),
                modifier = modifier,
                isOnline = accountInfo.firebaseIsActive,
            )

            val status3 = if (accountInfo.googleDriveIsActive) "Active" else "Not active"

            AccountStatusCard(
                title = "Google drive",
                status = status3,
                icon = getIconByKey(googleDriveIcon),
                iconTint = Color.Unspecified,
                showStatusDot = true,
                isOnline = accountInfo.googleDriveIsActive,
                modifier = modifier
            )

            val status4 = if (accountInfo.syncCompleted) "Active" else "Not active"

            AccountStatusCard(
                title = "Cloud sync",
                status = status4,
                icon = getIconByKey(cloudSyncIcon),
                iconTint = Color.Unspecified,
                showStatusDot = true,
                isOnline = accountInfo.syncCompleted,
                modifier = modifier
            )

            val modifierBtn = Modifier.padding(bottom = 10.dp, top = 10.dp)

            if (accountInfo.pending) {
                CircularProgressIndicator()
            } else {
                if (accountInfo.showGrantPermissions) {
                    Button(
                        modifier = modifierBtn, onClick = { onGrantPermissionClick() }) {
                        Text(text = "Grant permissions for Google Drive")
                    }
                }
            }

            Button(
                modifier = modifierBtn, onClick = { onSignOut() }) {
                Text(text = "Sing out")
            }

        }
    }
}

@Composable
fun AccountStatusCard(
    title: String,
    status: String,
    icon: Painter,
    iconTint: Color,
    showStatusDot: Boolean = true,
    isOnline: Boolean = true,
    modifier: Modifier
) {
    Card(
        modifier = modifier, colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = iconTint
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = status,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (showStatusDot) {
                Surface(
                    modifier = Modifier.size(10.dp),
                    shape = CircleShape,
                    color = if (isOnline) Color(0xFF4CAF50) else Color.Gray
                ) {}
            }
        }
    }
}