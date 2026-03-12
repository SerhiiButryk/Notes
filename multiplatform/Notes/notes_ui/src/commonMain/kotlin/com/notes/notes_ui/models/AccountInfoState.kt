package com.notes.notes_ui.models

import androidx.compose.runtime.Immutable

@Immutable
data class AccountInfoState(
    val email: String = "",
    val googleIsActive: Boolean = false,
    val firebaseIsActive: Boolean = false,
    val googleDriveIsActive: Boolean = false,
    val syncCompleted: Boolean = false,
    val showGrantPermissions: Boolean = false,
    val pending: Boolean = false,
)
