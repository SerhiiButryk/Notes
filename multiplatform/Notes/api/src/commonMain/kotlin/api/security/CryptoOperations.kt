package api.security

import api.Platform
import api.auth.AbstractAuthService
import api.auth.AuthCallback
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

private const val tag = "CryptoOperations"

abstract class CryptoOperations : AuthCallback {

    private val DERIVED_PASS_KEY = "derived_key_pass"

    private val SECRET_KEY_ALGORITHM = "AES/GCM/NoPadding"

    abstract suspend fun encrypt(message: String): String

    abstract suspend fun decrypt(message: String): String

    suspend fun encryptWithDerivedKey(message: String): String {

        val key = getKey()

        if (message.isEmpty()) return ""
        if (key.isEmpty()) return ""

        val secretKey = SecretKeySpec(key, "AES")
        val cipher = Cipher.getInstance(SECRET_KEY_ALGORITHM)

        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)
        val spec = GCMParameterSpec(128, iv)

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)
        val ciphertext = cipher.doFinal(message.toByteArray())

        return String(Platform().base64.encode(iv + ciphertext))
    }

    suspend fun decryptWithDerivedKey(message: String): String {

        val key = getKey()

        if (message.isEmpty()) return ""
        if (key.isEmpty()) return ""

        try {

            val decoded = Platform().base64.decode(message)

            val secretKey = SecretKeySpec(key, "AES")
            val cipher = Cipher.getInstance(SECRET_KEY_ALGORITHM)

            val iv = decoded.take(12)
            val spec = GCMParameterSpec(128, iv.toByteArray())

            val messageBytes = decoded.slice(12 until decoded.size)

            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            return String(cipher.doFinal(messageBytes.toByteArray()))

        } catch (e: Exception) {
            Platform().logger.loge("$tag: failed to decrypt: " + e.message)
            e.printStackTrace()
            return ""
        }
    }


    open fun onDestroy() {}

    override suspend fun onAuthCompleted(password: String, uid: String, force: Boolean) {
        val key = Platform().storage.get(DERIVED_PASS_KEY)
        if (key.isEmpty() || force) {
            genDerivedKey(password, uid)
            Platform().logger.logd("$tag:onAuthCompleted() derived key is created")
        } else {
            Platform().logger.logd("$tag:onAuthCompleted() derived key is present")
        }
    }

    override suspend fun addAuthCallbackFor(authService: AbstractAuthService) {
        val key = Platform().storage.get(DERIVED_PASS_KEY)
        if (key.isEmpty()) {
            authService.setAuthCallback(this)
            Platform().logger.logi("$tag:addAuthCallback() added")
        } else {
            Platform().logger.logi("$tag:addAuthCallback() no-op")
        }
    }

    protected suspend fun getKey(): ByteArray {

        val key = Platform().storage.get(DERIVED_PASS_KEY)

        if (key.isEmpty()) {
            throw IllegalStateException("No derived key")
        }

        val keyBytes = Platform().base64.decode(key)
        val decKey = Platform().crypto.decrypt(String(keyBytes))
        val realKey = Platform().base64.decode(decKey)

        return realKey
    }

    protected fun genDerivedKeyInter(password: String, uid: String): ByteArray {
        // Gen derived key which we will use as user personal key
        val input = password + uid
        val secretKeySpec = SecretKeySpec(uid.toByteArray(), "HmacSHA256")

        val mac = Mac.getInstance("HmacSHA256")
        mac.init(secretKeySpec)

        return mac.doFinal(input.toByteArray())
    }

    protected suspend fun genDerivedKey(password: String, uid: String) {

        val key = Platform().base64.encode(genDerivedKeyInter(password, uid))

        val encKey = Platform().crypto.encrypt(String(key))
        val key2 = Platform().base64.encode(encKey.toByteArray())

        val result = Platform().storage.save(String(key2), DERIVED_PASS_KEY)
        Platform().logger.logd("$tag:genDerivedKey() saved = $result")
    }

}