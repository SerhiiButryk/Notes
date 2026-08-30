package com.notes.notes_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notes.notes_ui.components.SidebarItem
import com.notes.notes_ui.models.AccountInfoState
import com.notes.notes_ui.settings.AccountSettings
import com.notes.notes_ui.settings.GeneralSettings

enum class SettingsTab {
    GENERAL,
    ACCOUNT,
}

@Preview(widthDp = 1200, heightDp = 800)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    accountInfo: AccountInfoState = AccountInfoState(),
    isDebugMode: Boolean = false,
    onDebugModeChanged: (isDebugMode: Boolean) -> Unit = {},
    onSignOut: () -> Unit = {},
) {
    var selectedTab by remember { mutableStateOf(SettingsTab.GENERAL) }

    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxSize()

    ) {

        // ---------------- Left Sidebar -----------------------

        Column(
            modifier = Modifier
                .width(240.dp)
                .fillMaxHeight()
                .padding(16.dp)
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            // ------------- Sidebar Navigation Items ---------------

            SidebarItem(
                icon = Icons.Default.Info,
                label = "General",
                isSelected = selectedTab == SettingsTab.GENERAL,
                onClick = { selectedTab = SettingsTab.GENERAL },
            )

            SidebarItem(
                icon = Icons.Default.AccountBox,
                label = "Account",
                isSelected = selectedTab == SettingsTab.ACCOUNT,
                onClick = { selectedTab = SettingsTab.ACCOUNT },
            )

            Spacer(modifier = Modifier.height(4.dp))
        }

        // Vertical Separator Line
        HorizontalDivider(
            modifier = Modifier
                .width(4.dp)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .fillMaxHeight(),
        )

        // -------------------- Main Content Area --------------------

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(horizontal = 10.dp, vertical = 10.dp)
        ) {
            when (selectedTab) {

                SettingsTab.GENERAL -> {
                    GeneralSettingsScreen(
                        title = "General",
                        onBackClick = onBackClick,
                    ) {
                        GeneralSettings(
                            isDebugMode = isDebugMode,
                            onDebugModeChanged = onDebugModeChanged,
                            onSignOut = onSignOut,
                        )
                    }
                }

                SettingsTab.ACCOUNT -> {
                    GeneralSettingsScreen(
                        title = "Account",
                        onBackClick = onBackClick,
                        alignment = Alignment.CenterHorizontally,
                    ) {
                        AccountSettings(accountInfo = accountInfo)
                    }
                }
            }
        }

    }
}

@Composable
private fun GeneralSettingsScreen(
    title: String,
    onBackClick: () -> Unit,
    alignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable () -> Unit,
) {
    Column {

        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {

            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }

            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp,
            )
        }

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(scrollState),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = alignment,
        ) {
            content()
        }
    }

}