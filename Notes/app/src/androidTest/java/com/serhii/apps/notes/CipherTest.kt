/**
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.serhii.core.security.Cipher
import com.serhii.core.security.impl.crypto.CryptoError
import com.serhii.core.security.impl.crypto.Result
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * Unit Tests for
 * @link com.serhii.core.security.Cipher
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class CipherTest {

    /**
     * Symmetric enc / dec
     *
     *  Test for
     *  @link com.serhii.core.security.Cipher#encryptSymmetricWithKey(String, String, byte[]])
     *  @link com.serhii.core.security.Cipher#decryptSymmetricWithKey(String, String, byte[]])
     *
     *  1. Test encrypts text using symmetric key
     *  2. Test decrypts text using symmetric key
     *  3. Test verifies expected results
     */
    @Test
    fun test01_encryptDecryptUsingOpenSSL() {

       Log.i(TAG, "test01_encryptDecryptUsingOpenSSL() IN")

        var cipher = Cipher(Cipher.CRYPTO_PROVIDER_OPENSSL)

        val originalMessage = "The quick brown fox jumps over the lazy dog"
        val key = "01234567890123456789012345678901"
        val iv = "0123456789012345"

        var encryptedMessage: Result
        var decryptedMessage: Result

        // Encrypt message
        encryptedMessage = cipher.encryptSymmetricWithKey(originalMessage, key, iv.toByteArray())

        Assert.assertFalse("Failed to encrypt", encryptedMessage.error != CryptoError.OK)

        // Create new object
        cipher = Cipher(Cipher.CRYPTO_PROVIDER_OPENSSL)

        // Decrypted message
        decryptedMessage = cipher.decryptSymmetricWithKey(encryptedMessage.message, key, iv.toByteArray())

        Assert.assertFalse("Failed to decrypt", decryptedMessage.error != CryptoError.OK)
        Assert.assertTrue("Text are not correct", decryptedMessage.message == originalMessage)

       Log.i(TAG, "test01_encryptDecryptUsingOpenSSL() OUT")
    }

    /**
     * Symmetric enc / dec
     *
     *  Test for
     *  @link com.serhii.core.security.Cipher#encryptSymmetric(String)
     *  @link com.serhii.core.security.Cipher#decryptSymmetric(String, byte[]])
     *
     *  1. Test creates 2 key (key A and key B) in crypto provider store
     *  2. Test encrypts / decrypts using key A
     *  3. Test encrypts / decrypts using key B
     *  4. Test verifies expected results
     */
    @Test
    fun test02_encryptDecryptUsingSecureStore() {

       Log.i(TAG, "test02_encryptDecryptUsingSecureStore() IN")

        val cipher = Cipher()
        cipher.createKey(SECRET_KET_TEST_A, false)
        cipher.createKey(SECRET_KET_TEST_B, false)

        cipher.selectKey(SECRET_KET_TEST_A)

        val message = "The quick brown fox jumps over the lazy dog"

        val encMessage: Result = cipher.encryptSymmetric(message)
        Assert.assertTrue("Failed to encrypt", encMessage.error == CryptoError.OK)

        val decMessage: Result = cipher.decryptSymmetric(encMessage.message, encMessage.iv)
        Assert.assertTrue("Failed to decrypt", decMessage.error == CryptoError.OK)

        Assert.assertEquals("Text are not correct", decMessage.message, message)

        cipher.selectKey(SECRET_KET_TEST_B)

        val message2 = "Very short story"

        val encMessage2: Result = cipher.encryptSymmetric(message2)
        Assert.assertTrue("Failed to encrypt", encMessage2.error == CryptoError.OK)

        val decMessage2: Result = cipher.decryptSymmetric(encMessage2.message, encMessage2.iv)
        Assert.assertTrue("Failed to decrypt", decMessage2.error == CryptoError.OK)

        Assert.assertEquals("Text are not correct", decMessage2.message, message2)

       Log.i(TAG, "test02_encryptDecryptUsingSecureStore() OUT")
    }

    companion object {
        val TAG: String = CipherTest::class.java.simpleName

        const val SECRET_KET_TEST_A = "Key1"
        const val SECRET_KET_TEST_B = "Key2"
    }

}
