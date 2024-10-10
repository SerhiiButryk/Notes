/*
 * Copyright 2024. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.core.security.impl

import android.util.Base64
import com.serhii.core.log.Log
import com.serhii.core.security.Crypto
import com.serhii.core.security.Crypto.Companion.KEY_SIZE
import com.serhii.core.security.Hash
import com.serhii.core.security.impl.crypto.Openssl

/**
 * Class which manages library encryption keys
 */
class KeyMaster internal constructor(private val providerOpenssl: Openssl) {

    private val TAG = "KeyMaster"

    // TODO: Probably need to encrypt it
    // For password flow
    private var DERIVED_KEY_1: ByteArray = ByteArray(0)
    // For biometric flow
    private var DERIVED_KEY_2: ByteArray = ByteArray(0)
    // For unlock flow
    private var DERIVED_KEY_3: ByteArray = ByteArray(0)

    /**
     * Create keys for data encryption
     */
    fun createKey(input: String) {
        Log.detail(TAG, "createKeys()")
        // 1. Generate derived key
        DERIVED_KEY_1 = providerOpenssl.genDerivedKey(input)
        val encodedDerivedKey = String(Base64.encode(DERIVED_KEY_1, Base64.NO_WRAP))

        // 2. Create application key which is a random value
        val appKey = providerOpenssl.getRandomValue(KEY_SIZE)
        val encodedAppKey = String(Base64.encode(appKey, Base64.NO_WRAP))

        // 3. Encrypt application key with derived key
        val encryptedKey = providerOpenssl.encrypt(encodedAppKey, encodedDerivedKey)

        // 4. Save application key
        _save(encryptedKey.message)
        Log.detail(TAG, "createKeys() done")
    }

    /**
     * Create keys for data encryption
     */
    fun createKey(cipher: javax.crypto.Cipher) {
        Log.detail(TAG, "createKeys(cipher)")
        val appKey = getAppKey()
        if (appKey.isNotEmpty()) {
            // 1. Create a random value for derived key
            val derivedKey = providerOpenssl.getRandomValue(KEY_SIZE)
            val encodedDerivedKey = String(Base64.encode(derivedKey, Base64.NO_WRAP))

            // 2. Encrypt and save it
            val encryptedDerivedKey: ByteArray
            try {
                encryptedDerivedKey = cipher.doFinal(encodedDerivedKey.toByteArray())
            } catch (e: Exception) {
                Log.error(TAG, "createKeys(cipher) failed to encrypt: $e")
                return
            }

            val encodedKey = String(Base64.encode(encryptedDerivedKey, Base64.NO_WRAP))
            _save3(encodedKey)

            // 2. Encrypt and save application key
            val encryptedAppKey = providerOpenssl.encrypt(appKey, encodedDerivedKey)

            _save2(encryptedAppKey.message)
        } else {
            Log.error(TAG, "createKey() key is empty")
        }
    }

    /**
     * Gen derived key from input
     */
    fun initKeys(input: String): Boolean {
        Log.detail(TAG, "initKeys()")
        DERIVED_KEY_1 = providerOpenssl.genDerivedKey(input)
        return true
    }

    /**
     * Sets derived key
     */
    fun initKeys(cipher: javax.crypto.Cipher): Boolean {
        Log.detail(TAG, "initKeys(cipher)")

        val encodedDerivedKey = _get3()

        val decodedKey = Base64.decode(encodedDerivedKey.toByteArray(), Base64.NO_WRAP)

        val decryptedDerivedKey: ByteArray
        try {
            decryptedDerivedKey = cipher.doFinal(decodedKey)
        } catch (e: Exception) {
            Log.error(TAG, "initKeys(cipher) failed to decrypt: $e")
            return false
        }

        // Decrypted derived key
        DERIVED_KEY_2 = decryptedDerivedKey

        return true
    }

    fun getApplicationSymmetricKey(): String {
        var appKey = getAppKey()

        if (appKey.isEmpty()) {
            appKey = getAppKey2()
        }

        if (appKey.isEmpty()) {
            appKey = getAppKey3()
        }

        if (appKey.isEmpty())
            throw IllegalStateException("Failed to get app key")

        Log.info(TAG, "getApplicationSymmetricKey() got app key")
        return appKey
    }

    fun saveIVForBiometric(iv: ByteArray) {
        _save4(String(Base64.encode(iv, Base64.NO_WRAP)))
    }

    fun getIVForBiometric(): ByteArray {
        val iv = _get4()
        return Base64.decode(iv.toByteArray(), Base64.NO_WRAP)
    }

    // Try to get application key which is associated with derived key 1
    private fun getAppKey(): String {
        Log.detail(TAG, "getAppKey()")
        val encryptedKey = _get()

        if (encryptedKey.isEmpty()) {
            Log.error(TAG, "getAppKey() failed to get app key")
            return ""
        }

        if (DERIVED_KEY_1.isEmpty()) {
            Log.error(TAG, "getAppKey() failed to get derived key")
            return ""
        }

        val encodedDerivedKey = String(
            Base64.encode(DERIVED_KEY_1, Base64.NO_WRAP)
        )

        val result = providerOpenssl.decrypt(encryptedKey, encodedDerivedKey)

        return result.message
    }

    // Try to get application key which is associated with derived key 2
    private fun getAppKey2(): String {
        Log.detail(TAG, "getAppKey2()")
        val encryptedKey = _get2()

        if (encryptedKey.isEmpty()) {
            Log.error(TAG, "getAppKey2() failed to get app key")
            return ""
        }

        if (DERIVED_KEY_2.isEmpty()) {
            Log.error(TAG, "getAppKey2() failed to get derived key")
            return ""
        }

        val result = providerOpenssl.decrypt(encryptedKey, String(DERIVED_KEY_2))

        return result.message
    }

    fun biometricsInitialized(): Boolean {
        return _get3().isNotEmpty() && _get2().isNotEmpty()
    }

    fun createUnlockKey() {
        val randomBytes = providerOpenssl.getRandomValue(10)
        val encodedUnlockKey = String(Base64.encode(randomBytes, Base64.NO_WRAP))

        val appKey = getAppKey()
        val encKey = Hash.hashMD5(encodedUnlockKey)

        val result = providerOpenssl.encrypt(appKey, encKey)

        _save5(result.message)
        _setUnlockKey(encodedUnlockKey)
    }

    fun initUnlockKey(key: String) {
        DERIVED_KEY_3 = key.toByteArray()
    }

    fun getUnlockKey(): String { return _getUnlockKey() }

    private fun getAppKey3(): String {
        Log.detail(TAG, "getAppKey3()")
        val encryptedKey = _get5()

        if (encryptedKey.isEmpty()) {
            Log.error(TAG, "getAppKey3() failed to get app key")
            return ""
        }

        if (DERIVED_KEY_3.isEmpty()) {
            Log.error(TAG, "getAppKey3() failed to get derived key")
            return ""
        }

        val decKey = Hash.hashMD5(String(DERIVED_KEY_3))
        val result = providerOpenssl.decrypt(encryptedKey, decKey)
        return result.message
    }

    // For test only
    fun clear() {
        DERIVED_KEY_1 = ByteArray(0)
        DERIVED_KEY_2 = ByteArray(0)
        DERIVED_KEY_3 = ByteArray(0)
    }

    fun clearData() {

        _clear()

        DERIVED_KEY_1 = ByteArray(0)
        DERIVED_KEY_2 = ByteArray(0)
        DERIVED_KEY_3 = ByteArray(0)
    }

    // Saves app key to a file storage
    private external fun _save(value: String)
    private external fun _save2(value: String)
    private external fun _save3(value: String)
    private external fun _save4(value: String)
    private external fun _save5(value: String)
    // Gets app key from a file storage
    private external fun _get(): String
    private external fun _get2(): String
    private external fun _get3(): String
    private external fun _get4(): String
    private external fun _get5(): String
    private external fun _getUnlockKey(): String
    private external fun _setUnlockKey(unlockKey: String)
    private external fun _clear()
}