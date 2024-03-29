/**
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.serhii.apps.notes.utils.TestUtility
import com.serhii.core.security.Cipher
import com.serhii.core.security.Hash
import com.serhii.core.security.impl.crypto.CryptoError
import com.serhii.core.security.impl.crypto.Result
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * Unit Tests for [com.serhii.core.security.Cipher] class
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class CipherTests {

    /**
     * Symmetric enc / dec short text message
     *
     *  Test for
     *  [com.serhii.core.security.Cipher.encryptSymmetric]
     *  [com.serhii.core.security.Cipher.decryptSymmetric]
     *
     *  1. Test encrypts text using symmetric key
     *  2. Test decrypts text using symmetric key
     *  3. Test verifies expected results
     */
    @Test
    fun test01_OpenSSL_encrypt_decrypt() {

       Log.i(TAG, "test01_OpenSSL_encrypt_decrypt() IN")

        var cipher = Cipher(Cipher.CRYPTO_PROVIDER_OPENSSL)

        val originalMessage = "The quick brown fox jumps over the lazy dog"
        val key = "01234567890123456789012345678901"
        val iv = "0123456789012345"

        var encryptedMessage: Result
        var decryptedMessage: Result

        // Encrypt message
        encryptedMessage = cipher.encryptSymmetric(originalMessage, key, iv.toByteArray())

        Assert.assertFalse("Failed to encrypt", encryptedMessage.error != CryptoError.OK)

        // Create new object
        cipher = Cipher(Cipher.CRYPTO_PROVIDER_OPENSSL)

        // Decrypted message
        decryptedMessage = cipher.decryptSymmetric(encryptedMessage.message, key, iv.toByteArray())

        Assert.assertFalse("Failed to decrypt", decryptedMessage.error != CryptoError.OK)
        Assert.assertTrue("Text are not correct", decryptedMessage.message == originalMessage)

       Log.i(TAG, "test01_OpenSSL_encrypt_decrypt() OUT")
    }

    /**
     * Symmetric enc / dec
     *
     *  Test for
     *  [com.serhii.core.security.Cipher.encryptSymmetric]
     *  [com.serhii.core.security.Cipher.decryptSymmetric]
     *
     *  1. Test creates 2 key (key A and key B) in crypto provider store
     *  2. Test encrypts / decrypts using key A
     *  3. Test encrypts / decrypts using key B
     *  4. Test verifies expected results
     */
    @Test
    fun test02_AndroidSecureStore_encrypt_decrypt() {

       Log.i(TAG, "test02_AndroidSecureStore_encrypt_decrypt() IN")

        val cipher = Cipher()
        cipher.createKey(SECRET_KET_TEST_A, false)
        cipher.createKey(SECRET_KET_TEST_B, false)

        cipher.selectKey(SECRET_KET_TEST_A)

        val message = "The quick brown fox jumps over the lazy dog"

        val encMessage: Result = cipher.encryptSymmetric(message)
        Assert.assertTrue("Failed to encrypt", encMessage.error == CryptoError.OK)

        val decMessage: Result = cipher.decryptSymmetric(encMessage.message, inputIV = encMessage.iv)
        Assert.assertTrue("Failed to decrypt", decMessage.error == CryptoError.OK)

        Assert.assertEquals("Text are not correct", decMessage.message, message)

        cipher.selectKey(SECRET_KET_TEST_B)

        val message2 = "Very short story"

        val encMessage2: Result = cipher.encryptSymmetric(message2)
        Assert.assertTrue("Failed to encrypt", encMessage2.error == CryptoError.OK)

        val decMessage2: Result = cipher.decryptSymmetric(encMessage2.message, inputIV = encMessage2.iv)
        Assert.assertTrue("Failed to decrypt", decMessage2.error == CryptoError.OK)

        Assert.assertEquals("Text are not correct", decMessage2.message, message2)

       Log.i(TAG, "test02_AndroidSecureStore_encrypt_decrypt() OUT")
    }

    /**
     * Symmetric enc / dec long text message
     *
     *  Test for
     *  [com.serhii.core.security.Cipher.encryptSymmetric]
     *  [com.serhii.core.security.Cipher.decryptSymmetric]
     *
     *  1. Test encrypts text using symmetric key
     *  2. Test decrypts text using symmetric key
     *  3. Test verifies expected results
     */
    @Test
    fun test03_OpenSSL_encrypt_decrypt() {
        Log.i(TAG, "test03_OpenSSL_encrypt_decrypt() IN")

        var cipher = Cipher(Cipher.CRYPTO_PROVIDER_OPENSSL)

        val originalMessage = TestUtility.readFileFromTestAssets("long_text_example.txt")
        Assert.assertFalse("Failed to read file  from assets", originalMessage.isEmpty())

        val key = "01234567890123456789012345678901"
        val iv = "0123456789012345"

        var encryptedMessage: Result
        var decryptedMessage: Result

        // Encrypt message
        encryptedMessage = cipher.encryptSymmetric(originalMessage, key, iv.toByteArray())

        Assert.assertFalse("Failed to encrypt", encryptedMessage.error != CryptoError.OK)

        // Create new object
        cipher = Cipher(Cipher.CRYPTO_PROVIDER_OPENSSL)

        // Decrypted message
        decryptedMessage = cipher.decryptSymmetric(encryptedMessage.message, key, iv.toByteArray())

        Assert.assertFalse("Failed to decrypt", decryptedMessage.error != CryptoError.OK)
        Assert.assertTrue("Text are not correct", decryptedMessage.message == originalMessage)

        Log.i(TAG, "test03_OpenSSL_encrypt_decrypt() OUT")
    }

    @Test
    fun test04_AndroidSecureStore_encrypt_decrypt_LongText() {
        Log.i(TAG, "test04_AndroidSecureStore_encrypt_decrypt_LongText() IN")

        val cipher = Cipher()
        cipher.createKey(SECRET_KET_TEST_A, false)
        cipher.createKey(SECRET_KET_TEST_B, false)

        cipher.selectKey(SECRET_KET_TEST_A)

        val message = TestUtility.readFileFromTestAssets("long_text_example.txt")
        Assert.assertFalse("Failed to read file  from assets", message.isEmpty())

        val encMessage: Result = cipher.encryptSymmetric(message)
        Assert.assertTrue("Failed to encrypt", encMessage.error == CryptoError.OK)

        val decMessage: Result = cipher.decryptSymmetric(encMessage.message, inputIV = encMessage.iv)
        Assert.assertTrue("Failed to decrypt", decMessage.error == CryptoError.OK)

        Assert.assertEquals("Text are not correct", decMessage.message, message)

        cipher.selectKey(SECRET_KET_TEST_B)

        val message2 = TestUtility.readFileFromTestAssets("long_text_example.txt")
        Assert.assertFalse("Failed to read file  from assets", message2.isEmpty())

        val encMessage2: Result = cipher.encryptSymmetric(message2)
        Assert.assertTrue("Failed to encrypt", encMessage2.error == CryptoError.OK)

        val decMessage2: Result = cipher.decryptSymmetric(encMessage2.message, inputIV = encMessage2.iv)
        Assert.assertTrue("Failed to decrypt", decMessage2.error == CryptoError.OK)

        Assert.assertEquals("Text are not correct", decMessage2.message, message2)

        Log.i(TAG, "test04_AndroidSecureStore_encrypt_decrypt_LongText() OUT")
    }

    @Test
    fun test05_double_Key_Creation() {
        Log.i(TAG, "test05_double_Key_Creation() IN")
        val cipher = Cipher()
        cipher.createKey(SECRET_KET_TEST_C, false)
        cipher.createKey(SECRET_KET_TEST_C, false)
        Log.i(TAG, "test05_double_Key_Creation() OUT")
    }

    @Test
    fun test06_select_Invalid_Provider() {
        Log.i(TAG, "test06_select_Invalid_Provider() IN")

        var exception: Exception? = null
        var isExceptionThrown = false
        try {

            // Expected result is 'IllegalArgumentException' exception
            val cipher = Cipher("Invalid")

        } catch (e: Exception) {
            exception = e
            isExceptionThrown = true
        }

        Assert.assertTrue("Unexpected result, should throw exception", isExceptionThrown)
        Assert.assertTrue("Exception type is wrong", exception is IllegalArgumentException)
        Assert.assertTrue("Wrong message", exception?.message == "Unknown crypto provider is passed")

        Log.i(TAG, "test06_select_Invalid_Provider() IN")
    }

    @Test
    fun test07_hash_MD5() {
        Log.i(TAG, "test07_hash_MD5() IN")

        val message1 = "0123456789"
        val hash = Hash()

        var result = hash.hashMD5(message1)
        var expectedResult = "781e5e245d69b566979b86e28d23f2c7"

        Assert.assertTrue("Failed to generate correct hash", result == expectedResult)

        val message2 = "Apple Inc. is an American multinational technology"

        result = hash.hashMD5(message2)
        expectedResult = "0b29dd825349b4f080e05991de4f3d29"

        Assert.assertTrue("Failed to generate correct hash", result == expectedResult)

        val message3 = "Apple Inc. is an American multinational technology" +
            " that specializes in consumer electronics, software and online services headquartered in " +
            "Cupertino, California, United States. Apple is the largest technology company by revenue" +
            " (totaling US365.8 billion in 2021) and"

        result = hash.hashMD5(message3)
        expectedResult = "ef62781f3cbd4199f4dffb73bff18d8e"

        Assert.assertTrue("Failed to generate correct hash", result == expectedResult)

        Log.i(TAG, "test07_hash_MD5() OUT")
    }

    @Test
    fun test08_AndroidSecureStore_encrypt_decrypt_DefaultKey() {
        Log.i(TAG, "test08_encryptDecryptUsingSecureStore_DefaultKey() IN")

        val cipher = Cipher()
        cipher.selectKey("Default-key-160375068")

        val message = TestUtility.readFileFromTestAssets("long_text_example.txt")
        Assert.assertFalse("Failed to read file  from assets", message.isEmpty())

        val encMessage: Result = cipher.encryptSymmetric(message)
        Assert.assertTrue("Failed to encrypt", encMessage.error == CryptoError.OK)

        val decMessage: Result = cipher.decryptSymmetric(encMessage.message, inputIV =  encMessage.iv)
        Assert.assertTrue("Failed to decrypt", decMessage.error == CryptoError.OK)

        Assert.assertEquals("Text are not correct", decMessage.message, message)

        Log.i(TAG, "test08_encryptDecryptUsingSecureStore_DefaultKey() OUT")
    }

    @Test
    fun test09_Simple_encrypt_decrypt_OpenSSL_short_key() {
        Log.i(TAG, "test09_Simple_encrypt_decrypt_OpenSSL_short_key() IN")

        val cipher = Cipher(Cipher.CRYPTO_PROVIDER_OPENSSL)

        val key = "a" // short key
        val message = "b" // short message

        val result = cipher.encrypt(message, key)

        Assert.assertNotNull(result)
        Assert.assertTrue(result.isNotEmpty())

        val cipher2 = Cipher(Cipher.CRYPTO_PROVIDER_OPENSSL)
        val result2 = cipher2.decrypt(result, key)

        Assert.assertNotNull(result2)
        Assert.assertTrue(result2.isNotEmpty())

        Assert.assertTrue(result2 == message)

        Log.i(TAG, "test09_Simple_encrypt_decrypt_OpenSSL_short_key() OUT")
    }

    @Test
    fun test10_Simple_encrypt_decrypt_OpenSSL_wrong_key() {
        Log.i(TAG, "test10_Simple_encrypt_decrypt_OpenSSL_wrong_key() IN")

        val cipher = Cipher(Cipher.CRYPTO_PROVIDER_OPENSSL)

        var key = "a" // short key
        val message = "b" // short message

        val result = cipher.encrypt(message, key)

        Assert.assertNotNull(result)
        Assert.assertTrue(result.isNotEmpty())

        key = "b"

        val cipher2 = Cipher(Cipher.CRYPTO_PROVIDER_OPENSSL)
        val result2 = cipher2.decrypt(result, key)

        Assert.assertTrue(result2.isEmpty())

        Log.i(TAG, "test10_Simple_encrypt_decrypt_OpenSSL_wrong_key() OUT")
    }

    @Test
    fun test09_Simple_encrypt_decrypt_SecureStore_short_key() {
        Log.i(TAG, "test09_Simple_encrypt_decrypt_SecureStore_short_key() IN")

        val cipher = Cipher()

        val key = "a" // short key
        val message = "b" // short message

        val result = cipher.encrypt(message, key)

        Assert.assertNotNull(result)
        Assert.assertTrue(result.isNotEmpty())

        val cipher2 = Cipher()
        val result2 = cipher2.decrypt(result, key)

        Assert.assertNotNull(result2)
        Assert.assertTrue(result2.isNotEmpty())

        Assert.assertTrue(result2 == message)

        Log.i(TAG, "test09_Simple_encrypt_decrypt_SecureStore_short_key() OUT")
    }

    companion object {
        private val TAG: String = CipherTests::class.java.simpleName

        private const val SECRET_KET_TEST_A = "Key1"
        private const val SECRET_KET_TEST_B = "Key2"
        private const val SECRET_KET_TEST_C = "Key3"
    }

}
