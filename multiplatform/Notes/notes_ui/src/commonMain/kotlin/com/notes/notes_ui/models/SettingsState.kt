package com.notes.notes_ui.models

import androidx.compose.runtime.Immutable

@Immutable
data class SettingsState(
    val isDebug: Boolean = false
)