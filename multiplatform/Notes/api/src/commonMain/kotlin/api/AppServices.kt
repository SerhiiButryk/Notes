package api

import api.auth.AuthCallback
import api.auth.AuthResult
import api.data.Document

/**
 * Application specific service declarations
 */

interface StorageService {
    suspend fun store(document: Document): Boolean

    suspend fun load(name: String): Document?

    suspend fun delete(name: String): Boolean

    suspend fun fetchAll(): List<Document>
}

interface AuthService {

    val name: String

    suspend fun createUser(
        pass: String,
        email: String,
    ): AuthResult = AuthResult.registrationFailed("")

    suspend fun login(
        pass: String,
        email: String,
    ): AuthResult = AuthResult.loginFailed()

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
}

object AppServices {

    private val authServices = mutableListOf<AuthService>()
    private var dataStoreService: StorageService? = null

    fun getAuthServiceByName(name: String): AuthService? {
        if (authServices.isEmpty()) return null
        for (item in authServices) {
            if (item.name == name) return item
        }
        return null
    }

    fun getDefaultAuthService(): AuthService {
        return getAuthServiceByName("firebase")!!
    }

    fun getStoreService(): StorageService {
        return dataStoreService!!
    }

    fun addService(
        storageService: StorageService,
    ) {
        dataStoreService = storageService
    }

    fun addService(
        authService: AuthService,
    ) {
        authServices.add(authService)
    }

}
