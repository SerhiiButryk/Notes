/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.security.impl.crypto

import com.serhii.core.CoreEngine.loadNativeLibrary
import com.serhii.core.security.Crypto

/**
 * Class provides OpenSSL interface for crypto operations
 */
internal class Openssl : BaseProvider() {

    override fun encryptSymmetric(message: String, inputIV: String, key: String): Result {
        checkInput(key, inputIV)

        val encryptedMessage = _encryptSymmetric(message, key, inputIV)

        return if (encryptedMessage.isEmpty()) {
             Result(error = CryptoError.UNKNOWN)
        } else {
            Result(message = encryptedMessage, iv = inputIV, error = CryptoError.OK)
        }
    }

    override fun decryptSymmetric(message: String, inputIV: String, key: String): Result {
        checkInput(key, inputIV)

        val decryptedMessage = _decryptSymmetric(message, key, inputIV)

        return if (decryptedMessage.isEmpty()) {
            Result(error = CryptoError.UNKNOWN)
        } else {
            Result(message = String(decryptedMessage), iv = inputIV, error = CryptoError.OK)
        }
    }

    override fun type(): String = Crypto.CRYPTO_PROVIDER_OPENSSL

    private fun checkInput(key: String, iv: String) {
        if (key.isEmpty() || key.length != BaseProvider.KEY_MAX_SIZE)
            throw IllegalArgumentException("Not a valid key. Please, prove a key with $KEY_MAX_SIZE length")

        if (iv.isEmpty() || iv.length != BaseProvider.IV_MAX_SIZE)
            throw IllegalArgumentException("Not a valid key. Please, prove an IV with $IV_MAX_SIZE length")
    }

    private external fun _encryptSymmetric(message: String, key: String, iv: String): String
    private external fun _decryptSymmetric(message: String, key: String, iv: String): ByteArray

    companion object {
        init {
            loadNativeLibrary()
        }
    }

}