package api.security

import api.auth.AuthCallback

interface CryptoOperations : AuthCallback {
    suspend fun encrypt(message: String): String

    suspend fun decrypt(message: String): String

    suspend fun encryptWithDerivedKey(message: String): String
    suspend fun decryptWithDerivedKey(message: String): String
}