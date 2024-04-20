/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.security

import com.serhii.core.CoreEngine
import com.serhii.core.log.Log
import com.serhii.core.security.impl.crypto.CryptoProvider
import com.serhii.core.security.impl.crypto.Result

/**
 * Class provides an interface to common crypto functionality.
 *
 * Note: You can select OpenSSL or Android implementation.
 */
class Crypto {

    private var provider: CryptoProvider

    // Uses default android crypto provider
    constructor() {
        Log.detail(TAG, "$TAG()")
        provider = CoreEngine.configure(this)
    }

    // User selected crypto provider
    constructor(_provider: String) {
        Log.detail(TAG, "$TAG(String), $_provider")
        provider = CoreEngine.configure(this, _provider)
    }

    fun selectProvider(_provider: String) {
        provider = CoreEngine.configure(this, _provider)
    }

    /**
     * Function for symmetric encryption which accepts IV data.
     * If key is empty then default key will be used
     * IV can be absent
     */
    fun encryptWithIV(message: String, key: String = "", inputIV: String = ""): Result {
        Log.detail(TAG, "encryptWithIV(), BB 2")
        return provider.encryptSymmetric(message, inputIV, key)
    }

    /**
     * Function for symmetric decryption which accepts IV data.
     * If key is empty then default key will be used
     */
    fun decryptWithIV(message: String, key: String = "", inputIV: String = ""): Result {
        Log.detail(TAG, "decryptWithIV(), BB 3")
        return provider.decryptSymmetric(message, inputIV, key)
    }

    fun selectKey(key: String) {
        Log.detail(TAG, "selectKey(), BB 4")
        provider.selectKey(key)
    }

    fun createKey(key: String, timeOutSeconds: Int = 0, authRequired: Boolean = false) {
        Log.detail(TAG, "createKey(), BB 7")
        provider.createKey(key, timeOutSeconds, authRequired)
    }

    fun getRandomValue(size: Int): ByteArray {
        Log.detail(TAG, "getRandomValue(), GRS")
        return provider.getRandomValue(size)
    }

    /**
     * Helper function for symmetric encryption.
     * It doesn't require IV data for input.
     * If key is empty then default key will be used
     */
    fun encrypt(message: String, key: String = ""): String {
        return provider.encrypt(message, key)
    }

    /**
     * Helper function for symmetric decryption.
     * It doesn't require IV data for input.
     * If key is empty then default key will be used
     */
    fun decrypt(message: String, key: String = ""): String {
        return provider.decrypt(message, key)
    }

    fun getKeyMaster() = CoreEngine.getKeyMaster()

    companion object {
        const val CRYPTO_PROVIDER_OPENSSL = "openssl"
        const val CRYPTO_PROVIDER_ANDROID = "android"
        const val TAG = "Crypto"
    }
}