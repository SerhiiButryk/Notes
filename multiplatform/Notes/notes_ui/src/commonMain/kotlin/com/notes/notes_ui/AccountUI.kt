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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import api.AppService
import api.AppServices
import com.notes.notes_ui.models.AccountInfoState
import com.notes.ui.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountUI(
    onBackClick: () -> Unit,
    onGrantPermissionClick: () -> Unit,
    accountInfo: AccountInfoState,
) {
    Scaffold(
        topBar = {
            SimpleTopBar(title = "Account Details", onBackClick = onBackClick)
        },
    ) { innerPadding ->

        val scrollState = rememberScrollState()

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(20.dp)
                    .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AccountUIImpl(
                accountInfo = accountInfo,
                onGrantPermissionClick = onGrantPermissionClick,
            )
        }
    }
}

@Composable
fun AccountUIImpl(
    accountInfo: AccountInfoState,
    onGrantPermissionClick: () -> Unit = {},
) {

    Spacer(modifier = Modifier.height(32.dp))

    // --- Profile Section ---

    Box(
        modifier =
            Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Profile Picture",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
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
        shape = RoundedCornerShape(12.dp),
    )

    Spacer(modifier = Modifier.height(16.dp))

    SuggestionChip(
        onClick = {},
        label = {
            Text(
                text = "Connected services:",
                fontSize = 18.sp,
            )
        },
    )

    Spacer(modifier = Modifier.height(16.dp))

    val modifier =
        Modifier
            .padding(vertical = 10.dp)
            .fillMaxWidth()

    if (AppServices.getServiceByKey(AppService.GOOGLE_AUTH) != null) {

        val status = if (accountInfo.googleIsActive) "Active" else "Not active"

        AccountStatusCard(
            title = "Google Account",
            status = status,
            painter = toPainter(googleIcon),
            modifier = modifier,
            isOnline = accountInfo.googleIsActive,
        )

    }

    AccountStatusCard(
        title = "Firebase Sync",
        status = if (accountInfo.firebaseIsActive) "Active" else "Not active",
        painter = toPainter(firebaseIcon),
        modifier = modifier,
        isOnline = accountInfo.firebaseIsActive,
    )

    if (AppServices.getServiceByKey(AppService.GOOGLE_STORAGE) != null) {

        val status = if (accountInfo.googleDriveIsActive) "Active" else "Not active"

        AccountStatusCard(
            title = "Google drive",
            status = status,
            painter = toPainter(googleDriveIcon),
            showStatusDot = true,
            isOnline = accountInfo.googleDriveIsActive,
            modifier = modifier,
        )

    }

    AccountStatusCard(
        title = "Cloud sync",
        status = if (accountInfo.syncCompleted) "Active" else "Not active",
        painter = toPainter(cloudSyncIcon),
        showStatusDot = true,
        isOnline = accountInfo.syncCompleted,
        modifier = modifier,
    )

    val modifierBtn = Modifier.padding(bottom = 10.dp, top = 10.dp)

    if (accountInfo.pending) {
        CircularProgressIndicator()
    } else {
        if (accountInfo.showGrantPermissions) {
            Button(
                modifier = modifierBtn,
                onClick = { onGrantPermissionClick() },
            ) {
                Text(text = "Grant permissions for Google Drive")
            }
        }
    }
}

@Composable
fun AccountStatusCard(
    title: String,
    status: String,
    painter: Painter,
    iconTint: Color = Color.Unspecified,
    showStatusDot: Boolean = true,
    isOnline: Boolean = true,
    modifier: Modifier,
) {
    Card(
        modifier = modifier,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = iconTint,
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = status,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            if (showStatusDot) {
                Surface(
                    modifier = Modifier.size(10.dp),
                    shape = CircleShape,
                    color = if (isOnline) Color(0xFF4CAF50) else Color.Gray,
                ) {}
            }
        }
    }
}
