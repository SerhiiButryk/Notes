package com.notes.app.security

import android.util.Base64
import api.CryptoOperations
import api.PlatformAPIs
import api.PlatformAPIs.logger
import api.auth.AuthCallback
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class CryptoProvider : CryptoOperations, AuthCallback {
    private val provider = CryptoKeystore()
    private val DERIVED_PASS_KEY = "derived_key_pass"

    @Synchronized // 'provider' is not thread safe
    override fun encrypt(message: String): String = provider.encrypt(message).message

    @Synchronized // 'provider' is not thread safe
    override fun decrypt(message: String): String = provider.decrypt(message).message

    override suspend fun encryptWithDerivedKey(message: String): String {
        return provider.encryptSymmetricWithKey(message = message, key = getKey())
    }

    override suspend fun decryptWithDerivedKey(message: String): String {
        return provider.decryptSymmetricWithKey(message = message, key = getKey())
    }

    override suspend fun onLoginCompleted(password: String, uid: String) {
        val key = PlatformAPIs.storage.get(DERIVED_PASS_KEY)
        if (key.isEmpty()) {
            genDerivedKey(password, uid)
            logger.logi("onLoginCompleted() key is stored")
        } else {
            logger.logi("onLoginCompleted() key is present")
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

        PlatformAPIs.storage.save(String(key2), DERIVED_PASS_KEY)
    }

    private suspend fun getKey(): ByteArray {
        val key = PlatformAPIs.storage.get(DERIVED_PASS_KEY)
        if (key.isEmpty()) {
            throw IllegalStateException("No derived key")
        }
        val keyBytes = Base64.decode(key, Base64.NO_WRAP)
        val decKey = provider.decrypt(String(keyBytes)).message
        val realKey = Base64.decode(decKey, Base64.NO_WRAP)
        return realKey
    }
}
