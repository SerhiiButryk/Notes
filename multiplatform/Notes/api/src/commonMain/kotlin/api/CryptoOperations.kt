package api

import api.auth.AuthCallback

interface CryptoOperations : AuthCallback {
    fun encrypt(message: String): String

    fun decrypt(message: String): String

    suspend fun encryptWithDerivedKey(message: String): String
    suspend fun decryptWithDerivedKey(message: String): String
}
