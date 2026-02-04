package com.notes.notes_ui.screens

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import api.PlatformAPIs.logger
import api.ui.CommonIcons
import com.notes.notes_ui.SettingsViewModel
import com.notes.ui.SimpleTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountUI(
    onBackClick: () -> Unit,
    onSignOut: () -> Unit,
    requestPermissions: (context: Any?, launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>) -> Unit,
    onActivityResult: () -> Unit,
    accountInfo: SettingsViewModel.AccountInfo,
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

            val activity = LocalActivity.current

            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartIntentSenderForResult()
            ) { result ->
                val result = result.resultCode == Activity.RESULT_OK
                logger.logi("AccountUI::activity result = $result")
                onActivityResult()
            }

            val modifier = Modifier.padding(vertical = 10.dp).fillMaxWidth()

            val status1 = if (accountInfo.googleIsActive) "Active" else "Not active"

            AccountStatusCard(
                title = "Google Account",
                status = status1,
                iconRes = CommonIcons.googleIcon,
                iconTint = Color.Unspecified,
                modifier = modifier,
                isOnline = accountInfo.googleIsActive,
            )

            val status2 = if (accountInfo.firebaseIsActive) "Active" else "Not active"

            AccountStatusCard(
                title = "Firebase Sync",
                status = status2,
                iconRes = CommonIcons.firebaseIcon,
                iconTint = Color(0xFFFFCA28),
                modifier = modifier,
                isOnline = accountInfo.firebaseIsActive,
            )

            val status3 = if (accountInfo.googleDriveIsActive) "Active" else "Not active"

            AccountStatusCard(
                title = "Cloud Storage",
                status = status3,
                iconRes = CommonIcons.googleDriveIcon,
                iconTint = Color.Unspecified,
                showStatusDot = true,
                isOnline = accountInfo.googleDriveIsActive,
                modifier = modifier
            )

            val modifierBtn = Modifier.padding(bottom = 10.dp, top = 10.dp)

            if (accountInfo.pending) {
                CircularProgressIndicator()
            } else {
                if (accountInfo.showGrantPermissions) {
                    Button(
                        modifier = modifierBtn, onClick = { requestPermissions(activity, launcher) }) {
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
    iconRes: Int,
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
                painter = painterResource(id = iconRes),
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