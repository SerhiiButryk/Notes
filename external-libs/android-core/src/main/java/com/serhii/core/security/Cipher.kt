/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.security

import android.util.Base64
import com.serhii.core.CoreEngine
import com.serhii.core.log.Log
import com.serhii.core.security.impl.crypto.CryptoProvider
import com.serhii.core.security.impl.crypto.Openssl
import com.serhii.core.security.impl.crypto.Result
import com.serhii.core.security.impl.crypto.SecureStore

/**
 * Class provides symmetric encryption functionality.
 * Note: You choose between OpenSSL or Android implementation.
 */
class Cipher {
    private var provider: CryptoProvider

    // Uses default android crypto provider
    constructor() {
        Log.detail(TAG, "Cipher()")
        provider = CoreEngine.configure(this)
    }

    // User selected crypto provider
    constructor(_provider: String) {
        Log.detail(TAG, "Cipher(String), $_provider")
        provider = CoreEngine.configure(this, _provider)
    }

    fun encryptSymmetric(message: String, key: String = "", inputIV: ByteArray = ByteArray(0)): Result {
        Log.detail(TAG, "encryptSymmetricWithKey(), BB 2")
        return provider.encryptSymmetric(message, inputIV, key)
    }

    fun decryptSymmetric(message: String, key: String = "", inputIV: ByteArray = ByteArray(0)): Result {
        Log.detail(TAG, "decryptSymmetricWithKey(), BB 3")
        return provider.decryptSymmetric(message, inputIV, key)
    }

    fun selectKey(key: String) {
        Log.detail(TAG, "selectKey(), BB 4")
        provider.selectKey(key)
    }

    fun createKey(key: String, timeOutSeconds: Int, authRequired: Boolean) {
        Log.detail(TAG, "createKey(), BB 5")
        provider.createKey(key, timeOutSeconds, authRequired)
    }

    fun createKey(key: String, authRequired: Boolean) {
        Log.detail(TAG, "createKey(), BB 6")
        provider.createKey(key, 0, authRequired)
    }

    fun createKey(key: String) {
        Log.detail(TAG, "createKey(), BB 7")
        provider.createKey(key, 0, false)
    }

    fun getRandomString(): String {
        Log.detail(TAG, "createKey(), GRS")
        val openSSLProvider = CoreEngine.configure(this, CRYPTO_PROVIDER_OPENSSL) as Openssl
        return openSSLProvider.getRandomString()
    }

    // Helper method to skip iv value handling
    fun encrypt(message: String, key: String = ""): String {
        val keyHash = Hash()
            .hashMD5(key)

        // Generate random iv
        // Expected length is 16
        val randomString = getRandomString().substring(0..15)
        val (result, _iv) = encryptSymmetric(message, keyHash, randomString.toByteArray())
        // Need base64 for SecureStore provider
        if (provider is SecureStore) {
            val iv = String(Base64.encode(_iv, Base64.NO_WRAP))
            val encMessage = iv + result
            return encMessage
        }
        val encMessage = String(_iv) + result
        return encMessage
    }
    // Helper method to skip iv value handling
    fun decrypt(message: String, key: String = ""): String {
        val keyHash = Hash()
            .hashMD5(key)

        // Getting iv value
        // Expected length is 16
        var iv = message.substring(0, 16)

        var _iv: ByteArray? = null
        if (provider is SecureStore) {
            _iv = Base64.decode(iv, Base64.NO_WRAP)
        }

        val actualMessage = message.substring(16)
        val (result) = decryptSymmetric(actualMessage, keyHash, _iv ?: iv.toByteArray())
        return result
    }

    companion object {
        const val CRYPTO_PROVIDER_OPENSSL = "openssl"
        const val CRYPTO_PROVIDER_ANDROID = "android"
        const val TAG = "Cipher"
    }
}