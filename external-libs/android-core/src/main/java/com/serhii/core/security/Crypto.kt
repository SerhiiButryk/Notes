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
 * Class provides an abstraction layer to different crypto APIs.
 *
 * Note:
 * You have a selection between OpenSSL or Android implementation.
 */
class Crypto {

    private var provider: CryptoProvider

    // With default android crypto provider
    constructor() {
        Log.detail(TAG, "$TAG()")
        provider = CoreEngine.configure(this)
    }

    // With selected crypto provider
    constructor(_provider: String) {
        Log.detail(TAG, "$TAG(String), $_provider")
        provider = CoreEngine.configure(this, _provider)
    }

    fun selectProvider(_provider: String) {
        provider = CoreEngine.configure(this, _provider)
    }

    /**
     * Calls symmetric encryption which accepts IV and key data.
     * If key is empty then the default key will be used
     * If IV is empty then it is generated automatically
     */
    fun encrypt(message: String, key: String = "", inputIV: String = ""): Result {
        Log.detail(TAG, "encryptWithIV(), BB 2")
        return provider.encrypt(message, key, inputIV)
    }

    /**
     * Calls symmetric decryption which accepts IV and key data.
     * If key is empty then the default key will be used
     * If IV is empty then it is retrieved from 'message' automatically
     */
    fun decrypt(message: String, key: String = "", inputIV: String = ""): Result {
        Log.detail(TAG, "decryptWithIV(), BB 3")
        return provider.decrypt(message, key, inputIV)
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

    fun getKeyMaster() = CoreEngine.getKeyMaster()

    companion object {

        const val CRYPTO_PROVIDER_OPENSSL = "openssl"
        const val CRYPTO_PROVIDER_ANDROID = "android"

        // Correct IV size
        const val IV_SIZE = 16
        // Correct key size
        const val KEY_SIZE = 32

        const val TAG = "Crypto"
    }
}