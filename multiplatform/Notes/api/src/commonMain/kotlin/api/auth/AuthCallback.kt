package api.auth

interface AuthCallback {
    suspend fun onLoginCompleted(password: String, uid: String, force: Boolean) {}
    fun onUserAction(data: Any?) {}
    suspend fun addAuthCallbackFor(authService: AbstractAuthService) {}
}