package com.notes.api

import com.notes.api.data.Notes

/**
 * Application specific service declarations
 */

interface StorageService {
    suspend fun store(
        name: String,
        value: String,
    ): Boolean

    suspend fun load(name: String): String?

    suspend fun delete(name: String): Boolean

    suspend fun fetchAll(): List<Notes>
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
}

internal var dataStoreService: StorageService? = null

internal var authService: AuthService? = null

fun initServices(
    storageServiceImpl: StorageService,
    authServiceImp: AuthService,
) {
    dataStoreService = storageServiceImpl
    authService = authServiceImp
}

/**
 * Factories for convenience
 */

fun provideDataStoreService(): StorageService = dataStoreService!!

fun provideAuthService(): AuthService = authService!!
