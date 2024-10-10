/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.security.impl.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.UserNotAuthenticatedException
import android.util.Base64
import com.serhii.core.log.Log
import com.serhii.core.security.Crypto
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Class provides Android keystore interface for crypto operations
 */
internal class AndroidProvider : BaseProvider() {

    private var keyStore: KeyStore? = null
    private var selectedKey: String? = null

    override fun selectKey(key: String) {
        Log.detail(TAG, "selectKey(): $key")
        // Throw exception in case of error. Cannot proceed.
        require(isSecretKeyExists(key)) { "No key with this alias ($key) is created" }
        selectedKey = key
    }

    override fun createKey(key: String, timeOutSeconds: Int, authRequired: Boolean) {
        Log.detail(TAG, "createKey()")
        init(key, timeOutSeconds, authRequired)
    }

    override fun encrypt(message: String, key: String, inputIV: String): Result {
        // Throw exception in case of error. Cannot proceed.
        checkNotNull(selectedKey) { "No key is selected for cryptographic operation" }
        try {
            val secretKey = keyStore?.getKey(selectedKey, null) as SecretKey
            val encryptionCipher = Cipher.getInstance(SECRET_KEY_ALGORITHM)
            encryptionCipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val _iv = encryptionCipher.iv
            val encryptedText = encryptionCipher.doFinal(message.toByteArray(StandardCharsets.UTF_8))
            val encryptedMessage = String(Base64.encode(encryptedText, Base64.NO_WRAP))
            val iv = String(Base64.encode(_iv, Base64.NO_WRAP))
            return Result(iv + encryptedMessage, iv, CryptoError.OK, true)
        } catch (e: UserNotAuthenticatedException) {
            Log.error(TAG, "user not authenticated exception: $e")
            e.printStackTrace()
            return Result(error = CryptoError.USER_NOT_AUTHORIZED)
        } catch (e: Exception) {
            Log.error(TAG, "exception during encryption: " + e.message)
            e.printStackTrace()
        }
        return Result(error = CryptoError.UNKNOWN)
    }

    override fun decrypt(message: String, key: String, inputIV: String): Result {
        // Throw exception in case of error. Cannot proceed.
        checkNotNull(selectedKey) { "No key is selected for cryptographic operation" }
        try {
            val secretKey = keyStore?.getKey(selectedKey, null) as SecretKey
            val cipher = Cipher.getInstance(SECRET_KEY_ALGORITHM)

            var _iv = inputIV
            var realMessage = message
            if (_iv.isEmpty()) {
                _iv = message.substring(0, Crypto.IV_SIZE)
                realMessage = message.substring(Crypto.IV_SIZE)
            }

            val iv = Base64.decode(_iv.toByteArray(), Base64.NO_WRAP)
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            val text = cipher.doFinal(Base64.decode(realMessage.toByteArray(), Base64.NO_WRAP))
            return Result(String(text), error = CryptoError.OK)
        } catch (e: UserNotAuthenticatedException) {
            Log.error(TAG, "user not authenticated exception: $e")
            e.printStackTrace()
            return Result(error = CryptoError.USER_NOT_AUTHORIZED)
        } catch (e: Exception) {
            Log.error(TAG, "exception during decryption: " + e.message)
            e.printStackTrace()
        }
        return Result(error = CryptoError.UNKNOWN)
    }

    private fun init(key: String, timeOutSeconds: Int, authRequired: Boolean) {
        if (!isSecretKeyExists(key)) {
            // Gen a secret key entity
            _createKey(key, timeOutSeconds, authRequired)
        }
    }

    private fun load(keyStore: KeyStore): Boolean {
        try {
            keyStore.load(null)
            return true
        } catch (e: CertificateException) {
            Log.error(TAG, "exception during loading keyStore: " + e.message)
            e.printStackTrace()
        } catch (e: IOException) {
            Log.error(TAG, "exception during loading keyStore: " + e.message)
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            Log.error(TAG, "exception during loading keyStore: " + e.message)
            e.printStackTrace()
        }
        return false
    }

    private fun isSecretKeyExists(key: String): Boolean {
        try {
            val entry: KeyStore.Entry? = keyStore?.getEntry(key, null)
            return entry != null
        } catch (e: KeyStoreException) {
            Log.error(TAG, "exception: " + e.message)
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            Log.error(TAG, "exception: " + e.message)
            e.printStackTrace()
        } catch (e: UnrecoverableEntryException) {
            Log.error(TAG, "exception: " + e.message)
            e.printStackTrace()
        }
        return false
    }

    private fun _createKey(key: String, timeOutSeconds: Int, authRequired: Boolean): Boolean {
        Log.detail(TAG, "_createKey(): $key $timeOutSeconds")
        val builder = KeyGenParameterSpec.Builder(
            key,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ) // More info on GCM - https://en.wikipedia.org/wiki/Galois/Counter_Mode
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM) // GCM doesn't use padding
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        // User authentication is needed to user this key
        if (authRequired) {
            builder.setUserAuthenticationRequired(true)
        }
        // Additional security
        if (timeOutSeconds != 0) {
            builder.setUserAuthenticationValidityDurationSeconds(timeOutSeconds)
        }
        val keyGenerator = getKeyGenerator(KeyProperties.KEY_ALGORITHM_AES)
        if (keyGenerator != null) {
            return genSecretKey(keyGenerator, builder.build())
        }
        Log.error(TAG, "_createKey(): failed to gen key")
        return false
    }

    private fun createKeyForBiometricAuth(): Boolean {

        val key = DEFAULT_KEY_FOR_BIOMETRIC

        if (isSecretKeyExists(key)) {
            Log.detail(TAG, "createKeyForBiometricAuth(): exists, return")
            return true
        }

        val keySpecs = KeyGenParameterSpec.Builder(
            key,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setUserAuthenticationRequired(true)
            // Invalidate the keys if the user has registered a new biometric
            // credential, such as a new fingerprint. Can call this method only
            // on Android 7.0 (API level 24) or higher. The variable
            // "invalidatedByBiometricEnrollment" is true by default.
            .setInvalidatedByBiometricEnrollment(true)
            .build()

        val keyGenerator = getKeyGenerator(KeyProperties.KEY_ALGORITHM_AES)

        var result = false
        if (keyGenerator != null) {
            result = genSecretKey(keyGenerator, keySpecs)
        }

        if (!result) {
            Log.error(TAG, "createKeyForBiometricAuth(): failed to gen key")
        }

        return result
    }

    private fun initKeyStore(): KeyStore {
        try {
            return KeyStore.getInstance("AndroidKeyStore")
        } catch (e: KeyStoreException) {
            Log.error(TAG, "exception: failed to init keyStore object" + e.message)
            e.printStackTrace()
            // Cannot proceed
            throw RuntimeException("Failed to init keystore", e)
        }
    }

    private fun getKeyGenerator(algorithm: String): KeyGenerator? {
        try {
            return KeyGenerator.getInstance(algorithm, "AndroidKeyStore")
        } catch (e: NoSuchAlgorithmException) {
            Log.error(TAG, "exception: failed to get Key Generator" + e.message)
            e.printStackTrace()
        } catch (e: NoSuchProviderException) {
            Log.error(TAG, "exception: failed to get Key Generator" + e.message)
            e.printStackTrace()
        }
        return null
    }

    private fun genSecretKey(
        keyGenerator: KeyGenerator,
        keyGenParameterSpec: KeyGenParameterSpec
    ): Boolean {
        try {
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        } catch (e: InvalidAlgorithmParameterException) {
            Log.error(TAG, "exception: failed to gen Secret Key: " + e.message)
            e.printStackTrace()
            return false
        }
        return true
    }

    override fun type(): String = com.serhii.core.security.Crypto.CRYPTO_PROVIDER_ANDROID

    fun getSecretKeyForBiometricAuthOrCreate(): SecretKey? {

        if (!isSecretKeyExists(DEFAULT_KEY_FOR_BIOMETRIC)) {
            createKeyForBiometricAuth()
        }

        return keyStore?.getKey(DEFAULT_KEY_FOR_BIOMETRIC, null) as? SecretKey
    }

    fun getCipherForBiometricAuth(): Cipher {
        return Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                + KeyProperties.BLOCK_MODE_CBC + "/"
                + KeyProperties.ENCRYPTION_PADDING_PKCS7)
    }

    companion object {
        private const val TAG = "AndroidProvider"
        private const val SECRET_KEY_ALGORITHM = "AES/GCM/NoPadding"
        private const val DEFAULT_KEY = "Default-key-160375068"
        private const val DEFAULT_KEY_FOR_BIOMETRIC = "Default-key-43294023"
    }

    init {
        try {
            keyStore = initKeyStore()
            val success = load(keyStore!!)
            createKey(DEFAULT_KEY, 0, false)
            selectKey(DEFAULT_KEY)
            Log.detail(TAG, "init(): key store loaded '$success'")
        } catch (e: Exception) {
            Log.error(TAG, "init(): failed to load, error = $e")
        }
    }

}