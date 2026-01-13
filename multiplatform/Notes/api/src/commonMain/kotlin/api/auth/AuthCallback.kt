package api.auth

interface AuthCallback {
    suspend fun onLoginCompleted(password: String, uid: String)
}