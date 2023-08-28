/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.security.impl.crypto

import android.util.Base64
import com.serhii.core.CoreEngine.loadNativeLibrary
import java.lang.RuntimeException
import java.security.SecureRandom

/**
 * Class provides OpenSSL interface for crypto operations
 */
internal class Openssl : CryptoProvider {

    override fun encryptSymmetric(message: String, inputIV: ByteArray, key: String?): Result {
        var resultData : Result
        val encryptedMessage = _encryptSymmetric(message, key ?: "", String(inputIV))
        if (encryptedMessage.isEmpty()) {
            resultData = Result(error = CryptoError.UNKNOWN)
        } else {
            resultData = Result(message = encryptedMessage, iv = inputIV, error = CryptoError.OK)
        }
        return resultData
    }

    override fun decryptSymmetric(message: String, inputIV: ByteArray, key: String?): Result {
        var resultData : Result
        val decryptedMessage = _decryptSymmetric(message, key ?: "", String(inputIV))
        if (decryptedMessage.isEmpty()) {
            resultData = Result(error = CryptoError.UNKNOWN)
        } else {
            resultData = Result(message = String(decryptedMessage), iv = inputIV, error = CryptoError.OK)
        }
        return resultData
    }

    override fun selectKey(key: String) {
        // No-op
        throw RuntimeException("Illegal operation with the provider")
    }

    override fun createKey(key: String, timeOutSeconds: Int, authRequired: Boolean) {
        // No-op
        throw RuntimeException("Illegal operation with the provider")
    }

    // TODO: Replace with openSSL calls
    fun getRandomString(): String {
        val byteArray = ByteArray(20)
        SecureRandom.getInstance("SHA1PRNG").nextBytes(byteArray)
        return String(Base64.encode(byteArray, Base64.NO_WRAP))
    }

    private external fun _encryptSymmetric(message: String, key: String, iv: String): String
    private external fun _decryptSymmetric(message: String, key: String, iv: String): ByteArray

    companion object {
        init {
            loadNativeLibrary()
        }
    }

}