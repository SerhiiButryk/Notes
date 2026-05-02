package com.notes.os.impl

import android.util.Base64
import api.Platform
import api.auth.AbstractAuthService
import api.security.CryptoOperations
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class CryptoProvider : CryptoOperations {
    private val provider = CryptoKeystore()
    private val DERIVED_PASS_KEY = "derived_key_pass"
    private val protectKeyStore = Mutex()

    // NOTE: This call is not thread safe because KeyStore is not thread safe
    // That's why we use mutex
    override suspend fun encrypt(message: String): String =
        protectKeyStore.withLock { provider.encrypt(message).message }

    // NOTE: This call is not thread safe because KeyStore is not thread safe
    // That's why we use mutex
    override suspend fun decrypt(message: String): String =
        protectKeyStore.withLock { provider.decrypt(message).message }

    override suspend fun encryptWithDerivedKey(message: String): String {
        return provider.encryptSymmetricWithKey(message = message, key = getKey())
    }

    override suspend fun decryptWithDerivedKey(message: String): String {
        return provider.decryptSymmetricWithKey(message = message, key = getKey())
    }

    override suspend fun onLoginCompleted(password: String, uid: String, force: Boolean) {
        val key = Platform().storage.get(DERIVED_PASS_KEY)
        if (key.isEmpty() || force) {
            genDerivedKey(password, uid)
            Platform().logger.logd("onLoginCompleted() key is created")
        } else {
            Platform().logger.logd("onLoginCompleted() key is present")
        }
    }

    override suspend fun addAuthCallbackFor(authService: AbstractAuthService) {
        val key = Platform().storage.get(DERIVED_PASS_KEY)
        if (key.isEmpty()) {
            authService.setAuthCallback(this)
            Platform().logger.logi("addAuthCallback() added")
        } else {
            Platform().logger.logi("addAuthCallback() no-op")
        }
    }

    private suspend fun genDerivedKey(password: String, uid: String) {
        // Gen derived key which we will use as user personal key
        val input = password + uid
        val secretKeySpec = SecretKeySpec(uid.toByteArray(), "HmacSHA256")

        val mac = Mac.getInstance("HmacSHA256")
        mac.init(secretKeySpec)

        val hmacBytes = mac.doFinal(input.toByteArray())
        val key = Base64.encode(hmacBytes, Base64.NO_WRAP)

        val encKey = provider.encrypt(String(key)).message
        val key2 = Base64.encode(encKey.toByteArray(), Base64.NO_WRAP)

        val result = Platform().storage.save(String(key2), DERIVED_PASS_KEY)
        Platform().logger.logd("genDerivedKey() saved = $result")
    }

    private suspend fun getKey(): ByteArray {
        val key = Platform().storage.get(DERIVED_PASS_KEY)
        if (key.isEmpty()) {
            throw IllegalStateException("No derived key")
        }
        val keyBytes = Base64.decode(key, Base64.NO_WRAP)
        val decKey = provider.decrypt(String(keyBytes)).message
        val realKey = Base64.decode(decKey, Base64.NO_WRAP)
        return realKey
    }

    suspend fun testOnly_genDerivedKey(password: String, uid: String) = genDerivedKey(password, uid)

    suspend fun testOnly_getKey() = getKey()
}