package com.notes.auth_ui.data

import api.PlatformAPIs
import api.PlatformAPIs.logger
import kotlinx.coroutines.flow.MutableStateFlow

private val userDataState = MutableStateFlow(UserInfo())

suspend fun loadUserData() {
    val userEmail = PlatformAPIs.storage.get(REGISTERED_USER_EMAIL)
    userDataState.value = UserInfo(email = userEmail)
    logger.logi("loadUserData() done")
}

fun isFirstLaunch(): Boolean {
    // TODO: Might not get correct result but works
    return userDataState.value.email.isEmpty()
}

fun isUserRegistered(): Boolean {
    // If we have saved used email then more likely user has already passed registration flow
    return userDataState.value.email.isNotEmpty()
}
