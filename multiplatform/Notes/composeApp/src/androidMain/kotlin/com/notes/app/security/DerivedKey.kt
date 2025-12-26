package com.notes.app.security

import com.notes.api.DerivedKeyOperations
import com.notes.api.PlatformAPIs
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

private val DEFAULT_KEY_LEN = 256 // bits
private val DEFAULT_KEY_ITERATIONS = 100
private val DEFAULT_KEY_SALT_LEN = 16 // bits

class DerivedKeyProvider : DerivedKeyOperations {

    override fun generatePDKey(input: String, salt: ByteArray): String {
        return generatePDKeyImpl(input, salt)
    }

    override fun generateSalt(): ByteArray {
        return generateSaltImpl()
    }

}

fun generatePDKeyImpl(
    input: String,
    salt: ByteArray,
    keyLen: Int = DEFAULT_KEY_LEN,
    iterations: Int = DEFAULT_KEY_ITERATIONS
): String {
    val keySpec = PBEKeySpec(input.toCharArray(), salt, iterations, keyLen)
    val keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
    return PlatformAPIs.base64.encode(keyFactory.generateSecret(keySpec).encoded)
}

fun generateSaltImpl(len: Int = DEFAULT_KEY_SALT_LEN): ByteArray {
    val bytes = ByteArray(len)
    SecureRandom().nextBytes(bytes)
    return bytes
}

