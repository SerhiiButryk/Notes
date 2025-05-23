package com.notes.auth_ui.data

import api.PlatformAPIs

/**
 * Local user data object
 */
internal data class UserInfo(val email: String = "")

internal const val REGISTERED_USER_EMAIL = "REGISTERED_USER_EMAIL"

internal suspend fun saveUserEmail(email: String) {
    PlatformAPIs.storage.save(email, REGISTERED_USER_EMAIL)
}

internal suspend fun getUserEmail(): String = PlatformAPIs.storage.get(REGISTERED_USER_EMAIL)