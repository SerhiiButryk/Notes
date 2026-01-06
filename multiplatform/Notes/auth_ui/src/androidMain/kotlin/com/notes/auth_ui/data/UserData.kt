package com.notes.auth_ui.data

import com.notes.api.PlatformAPIs
import com.notes.api.PlatformAPIs.logger
import kotlinx.coroutines.flow.MutableStateFlow

private const val REGISTERED_USER_EMAIL = "REGISTERED_USER_EMAIL"

private data class UserDataStore(
    val email: String = "",
)

private val userDataState = MutableStateFlow(UserDataStore())

internal suspend fun saveUserEmail(email: String) {
    PlatformAPIs.storage.save(email, REGISTERED_USER_EMAIL)
}

internal suspend fun getUserEmail(): String = PlatformAPIs.storage.get(REGISTERED_USER_EMAIL)

suspend fun loadUserData() {
    val userEmail = PlatformAPIs.storage.get(REGISTERED_USER_EMAIL)
    userDataState.value = UserDataStore(email = userEmail)
    logger.logi("loadUserData() done")
}

fun isFirstLaunch(): Boolean {
    // TODO: Might not get precise result but works
    return userDataState.value.email.isEmpty()
}

fun isUserRegistered(): Boolean {
    // If we have saved used email then more likely user was already passed registration flow
    return userDataState.value.email.isNotEmpty()
}
