package api.data

import api.PlatformAPIs
import api.PlatformAPIs.logger
import kotlinx.coroutines.flow.MutableStateFlow

private data class UserInfo(val email: String = "")

private val userDataState = MutableStateFlow(UserInfo())

private const val REGISTERED_USER_EMAIL = "REGISTERED_USER_EMAIL"

suspend fun saveUserEmail(email: String) {
    PlatformAPIs.storage.save(email, REGISTERED_USER_EMAIL)
}

suspend fun getUserEmail(): String = PlatformAPIs.storage.get(REGISTERED_USER_EMAIL)

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
    // If we have saved an email then it's more likely user has already passed registration
    return userDataState.value.email.isNotEmpty()
}
