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
    suspend fun createUser(
        pass: String,
        email: String,
    ): AuthResult

    suspend fun login(
        pass: String,
        email: String,
    ): AuthResult

    suspend fun sendEmailVerify(): AuthResult

    suspend fun isEmailVerified(): Boolean

    fun getUserEmail(): String

    fun isAuthenticated(): Boolean

    fun getUserId(): String

    fun setAuthCallback(callback: AuthCallback)
}

internal var dataStoreService: StorageService? = null

internal var authService: AuthService? = null

fun initServices(
    storageServiceImpl: StorageService,
    authServiceImp: AuthService,
    authCallback: AuthCallback
) {
    dataStoreService = storageServiceImpl
    authService = authServiceImp
    authService!!.setAuthCallback(authCallback)
}

/**
 * Factories for convenience
 */

fun provideDataStoreService(): StorageService = dataStoreService!!

fun provideAuthService(): AuthService = authService!!
