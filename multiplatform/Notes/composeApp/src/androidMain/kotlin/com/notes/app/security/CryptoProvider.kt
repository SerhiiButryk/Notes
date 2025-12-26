package com.notes.app.security

import com.notes.api.CryptoOperations

class CryptoProvider : CryptoOperations {

    private val provider = CryptoKeystore()

    @Synchronized // 'provider' is not thread safe
    override fun encrypt(message: String): String {
        return provider.encrypt(message).message
    }

    @Synchronized // 'provider' is not thread safe
    override fun decrypt(message: String): String {
        return provider.decrypt(message).message
    }
}