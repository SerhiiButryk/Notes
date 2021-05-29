package com.serhii.core

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.serhii.core.log.Log
import com.serhii.core.security.Cipher
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import com.serhii.core.security.impl.crypto.Result

@RunWith(AndroidJUnit4::class)
class CryptoTestSuit {

    @Before
    fun setup() {
        Log.setTag("LibCoreAndroid")
    }

    /**
     *  Verifies that symmetric encryption / decryption works fine for OpenSSL
     */
    @Test
    fun test01_SimpleEncryptDecrypt() {

        Log.info(TAG, "test01_SimpleEncryptDecrypt() IN")

        val cipher = Cipher(CoreEngine.CRYPTO_PROVIDER_OPENSSL)
        val message = "The quick brown fox jumps over the lazy dog"
        val key = "01234567890123456789012345678901"
        val iv = "0123456789012345"

        // Encrypt message
        var result = cipher.encryptSymmetricWithKey(message, key, iv.toByteArray())

        Assert.assertFalse("Failed to encrypt", result.message.isEmpty())

        // Decrypt message
        result = cipher.decryptSymmetricWithKey(result.message, key, iv.toByteArray())

        Assert.assertTrue("Failed to decrypt", result.message == message)

        Log.info(TAG, "test01_SimpleEncryptDecrypt() OUT")
    }

    /**
     *  Verifies that symmetric encryption / decryption works fine for SecureStore
     *
     *  1. Creates 2 keys
     *  2. Enc / dec using first key
     *  3. Enc / dec using second key
     */
    @Test
    fun test02_SimpleEncryptDecryptUsingSecureStore() {

        Log.info(TAG, "test02_SimpleEncryptDecryptUsingSecureStore() IN")

        val cipher = Cipher()
        cipher.createKey(SECRET_KET_TEST_A, false)
        cipher.createKey(SECRET_KET_TEST_B, false)

        cipher.selectKey(SECRET_KET_TEST_A)

        val message = "The quick brown fox jumps over the lazy dog"
        val encMessage: Result = cipher.encryptSymmetric(message)
        val decMessage: Result = cipher.decryptSymmetric(encMessage.message, encMessage.iv)

        Assert.assertEquals("Failed to decrypt message 1", decMessage.message, message)

        cipher.selectKey(SECRET_KET_TEST_B)

        val message2 = "Very short story"
        val encMessage2: Result = cipher.encryptSymmetric(message2)
        val decMessage2: Result = cipher.decryptSymmetric(encMessage2.message, encMessage2.iv)

        Assert.assertEquals("Failed to decrypt message 2", decMessage2.message, message2)

        Log.info(TAG, "test02_SimpleEncryptDecryptUsingSecureStore() OUT")
    }

    companion object {
        val TAG = CryptoTestSuit::class.java.simpleName

        val SECRET_KET_TEST_A = "Key1"
        val SECRET_KET_TEST_B = "Key2"
    }

}