package com.notes.notes_ui.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notes.notes_ui.AccountUIImpl
import com.notes.notes_ui.components.SwitchSettingItem
import com.notes.notes_ui.models.AccountInfoState

// General Settings View
@Composable
fun GeneralSettings(
    isDebugMode: Boolean,
    onDebugModeChanged: (isDebugMode: Boolean) -> Unit,
    onSignOut: () -> Unit,
) {

    SwitchSettingItem(
        title = "Debug",
        description = "Enable or disable debug mode",
        onCheckedChange = { value -> onDebugModeChanged(value) },
        checked = isDebugMode
    )

    Text(
        text = "Other settings",
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.onSurface,
    )

    Button(
        onClick = { onSignOut() },
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth(),
    ) {
        Text(text = "Sing out")
    }

}

// Account Settings View
@Composable
fun AccountSettings(
    accountInfo: AccountInfoState
) {
    AccountUIImpl(
        accountInfo = accountInfo,
    )
}