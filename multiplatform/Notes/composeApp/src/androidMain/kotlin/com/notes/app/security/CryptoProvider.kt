package com.notes.app.security

import api.CryptoOperations

class CryptoProvider : CryptoOperations {
    private val provider = CryptoKeystore()

    @Synchronized // 'provider' is not thread safe
    override fun encrypt(message: String): String = provider.encrypt(message).message

    @Synchronized // 'provider' is not thread safe
    override fun decrypt(message: String): String = provider.decrypt(message).message
}
