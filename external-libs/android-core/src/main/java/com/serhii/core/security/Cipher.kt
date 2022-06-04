/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.security

import com.serhii.core.security.impl.crypto.CryptoProvider
import com.serhii.core.CoreEngine
import com.serhii.core.log.Log
import com.serhii.core.security.impl.crypto.Result

/**
 * Class provides symmetric encryption functionality.
 * Note: You choose between OpenSSL or Android implementation.
 */
class Cipher {
    private var provider: CryptoProvider? = null

    // Uses default android crypto provider
    constructor() {
        Log.detail(TAG, "Cipher()")
        CoreEngine.configure(this)
    }

    // User selected crypto provider
    constructor(provider: String) {
        Log.detail(TAG, "Cipher(String), $provider")
        CoreEngine.configure(this, provider)
    }

    fun setCryptoProvider(impl: CryptoProvider) {
        Log.detail(TAG, "setCryptoProvider(), $impl")
        provider = impl
    }

    fun encryptSymmetric(message: String): Result {
        Log.detail(TAG, "encryptSymmetric(), AA")
        return provider?.encryptSymmetric(message, ByteArray(0))
            ?: throw IllegalStateException("Provider was not initialized")
    }

    fun decryptSymmetric(message: String, inputIV: ByteArray): Result {
        Log.detail(TAG, "decryptSymmetric(), BB")
        return provider?.decryptSymmetric(message, inputIV)
            ?: throw IllegalStateException("Provider was not initialized")
    }

    fun encryptSymmetricWithKey(message: String, key: String): Result {
        Log.detail(TAG, "encryptSymmetricWithKey(), BB 1")
        return provider?.encryptSymmetric(message, ByteArray(0), key)
            ?: throw IllegalStateException("Provider was not initialized")
    }

    fun encryptSymmetricWithKey(message: String, key: String, inputIV: ByteArray): Result {
        Log.detail(TAG, "encryptSymmetricWithKey(), BB 2")
        return provider?.encryptSymmetric(message, inputIV, key)
            ?: throw IllegalStateException("Provider was not initialized")
    }

    fun decryptSymmetricWithKey(message: String, key: String, inputIV: ByteArray): Result {
        Log.detail(TAG, "decryptSymmetricWithKey(), BB 3")
        return provider?.decryptSymmetric(message, inputIV, key)
            ?: throw IllegalStateException("Provider was not initialized")
    }

    fun selectKey(key: String) {
        Log.detail(TAG, "selectKey(), BB 4")
        provider?.selectKey(key) ?: throw IllegalStateException("Provider was not initialized")
    }

    fun createKey(key: String, timeOutSeconds: Int, authRequired: Boolean) {
        Log.detail(TAG, "createKey(), BB 5")
        provider?.createKey(key, timeOutSeconds, authRequired)
            ?: throw IllegalStateException("Provider was not initialized")
    }

    fun createKey(key: String, authRequired: Boolean) {
        Log.detail(TAG, "createKey(), BB 6")
        provider?.createKey(key, 0, authRequired)
            ?: throw IllegalStateException("Provider was not initialized")
    }

    companion object {
        const val CRYPTO_PROVIDER_OPENSSL = "openssl"
        const val CRYPTO_PROVIDER_ANDROID = "android"
        const val TAG = "Cipher"
    }
}