package com.notes.notes_ui.models

import androidx.compose.runtime.Immutable
import api.data.AppSettings

@Immutable
data class SettingsState(
    val isDebug: Boolean = AppSettings.isDebugEnabled
)