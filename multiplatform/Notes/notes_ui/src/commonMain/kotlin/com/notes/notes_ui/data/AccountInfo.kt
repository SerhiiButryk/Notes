package com.notes.notes_ui.data

data class AccountInfo(
    val email: String = "",
    val googleIsActive: Boolean = false,
    val firebaseIsActive: Boolean = false,
    val googleDriveIsActive: Boolean = false,
    val syncCompleted: Boolean = false,
    val showGrantPermissions: Boolean = false,
    val pending: Boolean = false,
)