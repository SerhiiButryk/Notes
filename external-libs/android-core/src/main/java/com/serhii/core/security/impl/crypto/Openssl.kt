/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.security.impl.crypto

import android.util.Base64
import com.serhii.core.CoreEngine
import com.serhii.core.CoreEngine.loadNativeLibrary
import com.serhii.core.log.Log
import com.serhii.core.security.Crypto
import com.serhii.core.security.Crypto.Companion.IV_SIZE
import com.serhii.core.security.Crypto.Companion.KEY_SIZE

/**
 * Class provides OpenSSL interface for crypto operations
 */
internal class Openssl : BaseProvider() {

     private fun encryptSymmetric(message: String, inputIV: String, key: String): Result {
        checkInput(key, inputIV)

        val encryptedMessage = _encryptSymmetric(message, key, inputIV)

        return if (encryptedMessage.isEmpty()) {
             Result(error = CryptoError.UNKNOWN)
        } else {
            Result(message = encryptedMessage, iv = inputIV, error = CryptoError.OK)
        }
    }

    private fun decryptSymmetric(message: String, inputIV: String, key: String): Result {
        checkInput(key, inputIV)

        val decryptedMessage = _decryptSymmetric(message, key, inputIV)

        return if (decryptedMessage.isEmpty()) {
            Result(error = CryptoError.UNKNOWN)
        } else {
            Result(message = String(decryptedMessage), iv = inputIV, error = CryptoError.OK)
        }
    }

    override fun encrypt(message: String, key: String, inputIV: String): Result {
        try {

            var _key = key
            if (_key.isEmpty()) {
                Log.info("Openssl", "encrypt() no key provided, try to use app key")
                _key = CoreEngine.getKeyMaster().getApplicationSymmetricKey()
            }

            val keyForEncrypt = _key.substring(0, KEY_SIZE)

            var isIVProvided = true

            var ivForEncrypt = inputIV
            if (ivForEncrypt.isEmpty()) {
                Log.info("Openssl", "encrypt() no iv provided, try to gen")
                isIVProvided = false
                val randomValue = getRandomValue(IV_SIZE)
                val encodedIV = String(Base64.encode(randomValue, Base64.NO_WRAP))
                ivForEncrypt = encodedIV.substring(0, IV_SIZE)
            }

            if (isIVProvided) {
                encryptSymmetric(message, ivForEncrypt, keyForEncrypt)
                return encryptSymmetric(message, ivForEncrypt, keyForEncrypt)
            } else {
                val result = encryptSymmetric(message, ivForEncrypt, keyForEncrypt)
                return Result(result.iv + result.message, result.iv, result.error, true)
            }

        } catch (e: Exception) {
            Log.error("Openssl", "encrypt() fatal error: $e")
        }
        return Result(error = CryptoError.UNKNOWN)
    }

    override fun decrypt(message: String, key: String, inputIV: String): Result {
        try {

            var _key = key
            if (_key.isEmpty()) {
                Log.info("Openssl", "decrypt() no key provided, try to use app key")
                _key = CoreEngine.getKeyMaster().getApplicationSymmetricKey()
            }

            var ivForDecrypt = inputIV
            var realMessage = message
            if (ivForDecrypt.isEmpty()) {
                Log.info("Openssl", "decrypt() no iv provided, try to get")
                ivForDecrypt = message.substring(0, IV_SIZE)
                realMessage = message.substring(IV_SIZE)
            }

            val keyForDecrypt = _key.substring(0, KEY_SIZE)

            return decryptSymmetric(realMessage, ivForDecrypt, keyForDecrypt)
        } catch (e: Exception) {
            Log.error("Openssl", "decrypt() fatal error: $e")
        }
        return Result(error = CryptoError.UNKNOWN)
    }

    override fun type(): String = Crypto.CRYPTO_PROVIDER_OPENSSL

    private fun checkInput(key: String, iv: String) {
        if (key.isEmpty() || key.length != KEY_SIZE)
            throw IllegalArgumentException("Not a valid key. Please, prove a key with $KEY_SIZE length")

        if (iv.isEmpty() || iv.length != IV_SIZE)
            throw IllegalArgumentException("Not a valid key. Please, prove an IV with $IV_SIZE length")
    }

    private external fun _encryptSymmetric(message: String, key: String, iv: String): String
    private external fun _decryptSymmetric(message: String, key: String, iv: String): ByteArray

    companion object {
        init {
            loadNativeLibrary()
        }
    }

}