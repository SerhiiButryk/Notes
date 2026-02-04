package api.auth

import java.lang.ref.WeakReference

interface AuthService {

    val name: String

    suspend fun createUser(
        pass: String,
        email: String,
    ): AuthResult = AuthResult.registrationFailed("")

    suspend fun login(
        pass: String,
        email: String,
        activityContext: Any?
    ): AuthResult = AuthResult.loginFailed()

    suspend fun login(
        tokenId: String,
        activityContext: Any?
    ): AuthResult = AuthResult.loginFailed()

    suspend fun sendEmailVerify(): AuthResult = AuthResult.verificationSentFailed("")

    suspend fun isEmailVerified(): Boolean = false

    fun getUserEmail(): String = ""

    fun isAuthenticated(): Boolean

    fun getUserId(): String = ""

    fun setAuthCallback(callback: AuthCallback?) {}

    fun init(context: Any?) {}

    suspend fun signOut(): Boolean

    fun resetSettings() {}
}

abstract class AbstractAuthService : AuthService {

    private var _callback: WeakReference<AuthCallback>? = null

    protected var callback: AuthCallback?
        get() {
            return _callback?.get()
        }
        private set(value) {}

    override fun setAuthCallback(callback: AuthCallback?) {
        this._callback = WeakReference(callback)
    }
}
