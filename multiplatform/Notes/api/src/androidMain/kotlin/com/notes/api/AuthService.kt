package com.notes.api

interface AuthService {
    suspend fun createUser(
        pass: String, email: String
    ): AuthResult

    suspend fun login(pass: String, email: String): AuthResult
    suspend fun sendEmailVerify(): AuthResult
    suspend fun isEmailVerified(): Boolean
    fun getUserEmail(): String
}