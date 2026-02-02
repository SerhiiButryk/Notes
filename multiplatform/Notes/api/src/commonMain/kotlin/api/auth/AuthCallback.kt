package api.auth

import api.AuthService

interface AuthCallback {
    suspend fun onLoginCompleted(password: String, uid: String)
    suspend fun addAuthCallbackFor(authService: AuthService)
}