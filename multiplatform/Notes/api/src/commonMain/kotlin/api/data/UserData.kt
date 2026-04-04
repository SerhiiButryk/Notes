package api.data

import api.Platform
import kotlinx.coroutines.flow.MutableStateFlow

data class UserInfo(
    val email: String = "",
    val code: String = ""
)

val userDataState = MutableStateFlow(UserInfo())

private const val REGISTERED_USER_EMAIL = "REGISTERED_USER_EMAIL"

suspend fun saveUserEmail(email: String) {
    Platform().storage.save(email, REGISTERED_USER_EMAIL)
}

suspend fun getUserEmail(): String = Platform().storage.get(REGISTERED_USER_EMAIL)

suspend fun loadUserData() {
    val email = Platform().storage.get(REGISTERED_USER_EMAIL)
    userDataState.value = userDataState.value.copy(email = email)
    Platform().logger.logi("loadUserData() done")
}

// TODO: Might not get correct result but it's just a hint
fun isFirstLaunch(): Boolean {
    return userDataState.value.email.isEmpty()
}

fun cacheCode(code: String) {
    userDataState.value = userDataState.value.copy(code = code)
}
