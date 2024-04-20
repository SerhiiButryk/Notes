/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.security.impl.crypto

import android.util.Base64
import com.serhii.core.CoreEngine
import com.serhii.core.log.Log
import java.lang.RuntimeException
import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Interface for provider of cryptography operations
 */
internal interface CryptoProvider {
    fun encryptSymmetric(message: String, inputIV: String, key: String = ""): Result
    fun decryptSymmetric(message: String, inputIV: String, key: String = ""): Result
    fun encrypt(message: String, key: String = ""): String
    fun decrypt(message: String, key: String = ""): String
    fun getRandomValue(size: Int) = ByteArray(0)

    // Should be overridden in derived class
    fun selectKey(key: String): Unit = throw RuntimeException("Illegal operation with the provider")
    // Should be overridden in derived class
    fun createKey(key: String, timeOutSeconds: Int, authRequired: Boolean): Unit = throw RuntimeException("Illegal operation with the provider")
    // Should be overridden in derived class
    fun type() = ""
    fun genDerivedKey(input: String) = ByteArray(0)
}

/**
 * Base class for provider of cryptography operations
 */
internal abstract class BaseProvider : CryptoProvider {

    companion object {
        // Acceptable IV size
        const val IV_MAX_SIZE = 16
        // Acceptable key size
        const val KEY_MAX_SIZE = 32
    }

    // Helper method to skip iv value handling on upper layer
    override fun encrypt(message: String, key: String): String {
        try {

            var _key = key
            if (_key.isEmpty()) {
                Log.info("BaseProvider", "encrypt() try to use app key")
                _key = CoreEngine.getKeyMaster().getApplicationSymmetricKey()
            }

            val keyForEncrypt = _key.substring(0, KEY_MAX_SIZE)
            val randomValue = getRandomValue(IV_MAX_SIZE)
            val encodedIV = String(Base64.encode(randomValue, Base64.NO_WRAP))
            val ivForEncrypt = encodedIV.substring(0, IV_MAX_SIZE)

            val (result, _iv) = encryptSymmetric(message, ivForEncrypt, keyForEncrypt)
            return _iv + result
        } catch (e: Exception) {
            Log.info("BaseProvider", "encrypt() fatal error: $e")
        }
        return ""
    }

    // Helper method to skip iv value handling on upper layer
    override fun decrypt(message: String, key: String): String {
        try {

            var _key = key
            if (_key.isEmpty()) {
                Log.info("BaseProvider", "decrypt() try to use app key")
                _key = CoreEngine.getKeyMaster().getApplicationSymmetricKey()
            }

            val ivForDecrypt = message.substring(0, IV_MAX_SIZE)
            val keyForDecrypt = _key.substring(0, KEY_MAX_SIZE)
            val realMessage = message.substring(IV_MAX_SIZE)

            val (result) = decryptSymmetric(realMessage, ivForDecrypt, keyForDecrypt)
            return result
        } catch (e: Exception) {
            Log.info("BaseProvider", "decrypt() fatal error: $e")
        }
        return ""
    }

    // TODO: Consider replacement with OpenSSL APIs
    override fun getRandomValue(size: Int): ByteArray {
        val byteArray = ByteArray(size)
        SecureRandom.getInstance("SHA1PRNG").nextBytes(byteArray)
        return byteArray
    }

    // TODO: Consider other solutions here
    override fun genDerivedKey(input: String): ByteArray {
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(input.toByteArray())
    }

}