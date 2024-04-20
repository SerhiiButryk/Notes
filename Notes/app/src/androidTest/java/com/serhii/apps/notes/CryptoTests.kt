/**
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.serhii.apps.notes.utils.TestUtility
import com.serhii.core.security.Crypto
import com.serhii.core.security.Hash
import com.serhii.core.security.impl.crypto.CryptoError
import com.serhii.core.security.impl.crypto.Result
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * Unit Tests for [com.serhii.core.security.Crypto] class
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class CryptoTests {

    /**
     * Symmetric enc / dec short text message
     *
     *  Test for
     *  [com.serhii.core.security.Crypto.encrypt]
     *  [com.serhii.core.security.Crypto.decrypt]
     *
     *  1. Test encrypts text using symmetric key
     *  2. Test decrypts text using symmetric key
     *  3. Test verifies expected results
     */
    @Test
    fun test01_OpenSSL_encrypt_decrypt() {

       Log.i(TAG, "test01_OpenSSL_encrypt_decrypt() IN")

        var crypto = Crypto(Crypto.CRYPTO_PROVIDER_OPENSSL)

        val originalMessage = "The quick brown fox jumps over the lazy dog"
        val key = "01234567890123456789012345678901"
        val iv = "0123456789012345"

        var encryptedMessage: Result
        var decryptedMessage: Result

        // Encrypt message
        encryptedMessage = crypto.encryptWithIV(originalMessage, key, iv)

        Assert.assertFalse("Failed to encrypt", encryptedMessage.error != CryptoError.OK)

        // Create new object
        crypto = Crypto(Crypto.CRYPTO_PROVIDER_OPENSSL)

        // Decrypted message
        decryptedMessage = crypto.decryptWithIV(encryptedMessage.message, key, iv)

        Assert.assertFalse("Failed to decrypt", decryptedMessage.error != CryptoError.OK)
        Assert.assertTrue("Text are not correct", decryptedMessage.message == originalMessage)

       Log.i(TAG, "test01_OpenSSL_encrypt_decrypt() OUT")
    }

    /**
     * Symmetric enc / dec
     *
     *  Test for
     *  [com.serhii.core.security.Crypto.encrypt]
     *  [com.serhii.core.security.Crypto.decrypt]
     *
     *  1. Test creates 2 key (key A and key B) in crypto provider store
     *  2. Test encrypts / decrypts using key A
     *  3. Test encrypts / decrypts using key B
     *  4. Test verifies expected results
     */
    @Test
    fun test02_Android_provider_encrypt_decrypt() {

       Log.i(TAG, "test02_Android_provider_encrypt_decrypt() IN")

        val crypto = Crypto()
        crypto.createKey(SECRET_KET_TEST_A)
        crypto.createKey(SECRET_KET_TEST_B)

        crypto.selectKey(SECRET_KET_TEST_A)

        val message = "The quick brown fox jumps over the lazy dog"

        val encMessage: Result = crypto.encryptWithIV(message)
        Assert.assertTrue("Failed to encrypt", encMessage.error == CryptoError.OK)

        val decMessage: Result = crypto.decryptWithIV(encMessage.message, inputIV = encMessage.iv)
        Assert.assertTrue("Failed to decrypt", decMessage.error == CryptoError.OK)

        Assert.assertEquals("Text are not correct", decMessage.message, message)

        crypto.selectKey(SECRET_KET_TEST_B)

        val message2 = "Very short story"

        val encMessage2: Result = crypto.encryptWithIV(message2)
        Assert.assertTrue("Failed to encrypt", encMessage2.error == CryptoError.OK)

        val decMessage2: Result = crypto.decryptWithIV(encMessage2.message, inputIV = encMessage2.iv)
        Assert.assertTrue("Failed to decrypt", decMessage2.error == CryptoError.OK)

        Assert.assertEquals("Text are not correct", decMessage2.message, message2)

       Log.i(TAG, "test02_Android_provider_encrypt_decrypt() OUT")
    }

    /**
     * Symmetric enc / dec long text message
     *
     *  Test for
     *  [com.serhii.core.security.Crypto.encrypt]
     *  [com.serhii.core.security.Crypto.decrypt]
     *
     *  1. Test encrypts text using symmetric key
     *  2. Test decrypts text using symmetric key
     *  3. Test verifies expected results
     */
    @Test
    fun test03_OpenSSL_encrypt_decrypt() {
        Log.i(TAG, "test03_OpenSSL_encrypt_decrypt() IN")

        var crypto = Crypto(Crypto.CRYPTO_PROVIDER_OPENSSL)

        val originalMessage = TestUtility.readFileFromTestAssets("long_text_example.txt")
        Assert.assertFalse("Failed to read file  from assets", originalMessage.isEmpty())

        val key = "01234567890123456789012345678901"
        val iv = "0123456789012345"

        var encryptedMessage: Result
        var decryptedMessage: Result

        // Encrypt message
        encryptedMessage = crypto.encryptWithIV(originalMessage, key, iv)

        Assert.assertFalse("Failed to encrypt", encryptedMessage.error != CryptoError.OK)

        // Create new object
        crypto = Crypto(Crypto.CRYPTO_PROVIDER_OPENSSL)

        // Decrypted message
        decryptedMessage = crypto.decryptWithIV(encryptedMessage.message, key, iv)

        Assert.assertFalse("Failed to decrypt", decryptedMessage.error != CryptoError.OK)
        Assert.assertTrue("Text are not correct", decryptedMessage.message == originalMessage)

        Log.i(TAG, "test03_OpenSSL_encrypt_decrypt() OUT")
    }

    @Test
    fun test04_Android_provider_encrypt_decrypt_LongText() {
        Log.i(TAG, "test04_Android_provider_encrypt_decrypt_LongText() IN")

        val crypto = Crypto()
        crypto.createKey(SECRET_KET_TEST_A)
        crypto.createKey(SECRET_KET_TEST_B)

        crypto.selectKey(SECRET_KET_TEST_A)

        val message = TestUtility.readFileFromTestAssets("long_text_example.txt")
        Assert.assertFalse("Failed to read file  from assets", message.isEmpty())

        val encMessage: Result = crypto.encryptWithIV(message)
        Assert.assertTrue("Failed to encrypt", encMessage.error == CryptoError.OK)

        val decMessage: Result = crypto.decryptWithIV(encMessage.message, inputIV = encMessage.iv)
        Assert.assertTrue("Failed to decrypt", decMessage.error == CryptoError.OK)

        Assert.assertEquals("Text are not correct", decMessage.message, message)

        crypto.selectKey(SECRET_KET_TEST_B)

        val message2 = TestUtility.readFileFromTestAssets("long_text_example.txt")
        Assert.assertFalse("Failed to read file  from assets", message2.isEmpty())

        val encMessage2: Result = crypto.encryptWithIV(message2)
        Assert.assertTrue("Failed to encrypt", encMessage2.error == CryptoError.OK)

        val decMessage2: Result = crypto.decryptWithIV(encMessage2.message, inputIV = encMessage2.iv)
        Assert.assertTrue("Failed to decrypt", decMessage2.error == CryptoError.OK)

        Assert.assertEquals("Text are not correct", decMessage2.message, message2)

        Log.i(TAG, "test04_Android_provider_encrypt_decrypt_LongText() OUT")
    }

    @Test
    fun test05_double_Key_Creation() {
        Log.i(TAG, "test05_double_Key_Creation() IN")
        val cipher = Crypto()
        cipher.createKey(SECRET_KET_TEST_C)
        cipher.createKey(SECRET_KET_TEST_C)
        Log.i(TAG, "test05_double_Key_Creation() OUT")
    }

    @Test
    fun test06_select_Invalid_Provider() {
        Log.i(TAG, "test06_select_Invalid_Provider() IN")

        var exception: Exception? = null
        var isExceptionThrown = false
        try {

            // Expected result is 'IllegalArgumentException' exception
            Crypto("Invalid")

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
    fun test08_Android_provider_encrypt_decrypt_DefaultKey() {
        Log.i(TAG, "test08_Android_provider_encrypt_decrypt_DefaultKey() IN")

        val crypto = Crypto()
        crypto.selectKey("Default-key-160375068")

        val message = TestUtility.readFileFromTestAssets("long_text_example.txt")
        Assert.assertFalse("Failed to read file  from assets", message.isEmpty())

        val encMessage: Result = crypto.encryptWithIV(message)
        Assert.assertTrue("Failed to encrypt", encMessage.error == CryptoError.OK)

        val decMessage: Result = crypto.decryptWithIV(encMessage.message, inputIV =  encMessage.iv)
        Assert.assertTrue("Failed to decrypt", decMessage.error == CryptoError.OK)

        Assert.assertEquals("Text are not correct", decMessage.message, message)

        Log.i(TAG, "test08_Android_provider_encrypt_decrypt_DefaultKey() OUT")
    }

    @Test
    fun test09_Simple_encrypt_decrypt_OpenSSL_short_key() {
        Log.i(TAG, "test09_Simple_encrypt_decrypt_OpenSSL_short_key() IN")

        val crypto = Crypto(Crypto.CRYPTO_PROVIDER_OPENSSL)

        val key = "a" // short key
        val message = "b" // short message

        // Should fail as we need a 32 len key
        val result = crypto.encrypt(message, key)

        Assert.assertNotNull(result)
        Assert.assertTrue(result.isEmpty())

        Log.i(TAG, "test09_Simple_encrypt_decrypt_OpenSSL_short_key() OUT")
    }

    @Test
    fun test10_Simple_encrypt_decrypt_Android_provider() {
        Log.i(TAG, "test11_Simple_encrypt_decrypt_Android_provider() IN")

        val crypto = Crypto(Crypto.CRYPTO_PROVIDER_ANDROID)

        val key = "dhwidiqwduhiwudhiqwhdiqhwidhqwiudhiwhdqihdwqidwihq"
        val message = "The quick brown fox jumps over the lazy dog"

        val result = crypto.encrypt(message, key)

        Assert.assertNotNull(result)
        Assert.assertTrue(result.isNotEmpty())

        val crypto2 = Crypto(Crypto.CRYPTO_PROVIDER_ANDROID)

        val decryptedMessage = crypto2.decrypt(result, key)

        Assert.assertNotNull(decryptedMessage)
        Assert.assertTrue(decryptedMessage == message)

        Log.i(TAG, "test11_Simple_encrypt_decrypt_Android_provider() OUT")
    }

    @Test
    fun test11_Simple_encrypt_decrypt_OpenSSL_wrong_key() {
        Log.i(TAG, "test11_Simple_encrypt_decrypt_OpenSSL_wrong_key() IN")

        val crypto = Crypto(Crypto.CRYPTO_PROVIDER_OPENSSL)

        var key = "diwdhuwdhiwwoepwrowmflemlemf;efe;fme;l;esfdfdsfefdsvvsdvsdvvsdv"
        val message = "b" // short message

        val result = crypto.encrypt(message, key)

        Assert.assertNotNull(result)
        Assert.assertTrue(result.isNotEmpty())

        key = "03ri094jeignoioneorgneroeirnoengorongeorognerogneorgenorngoe4p9-t0tt0i4-i"

        val crypto1 = Crypto(Crypto.CRYPTO_PROVIDER_OPENSSL)
        val result2 = crypto1.decrypt(result, key)

        Assert.assertNotNull(result2)
        Assert.assertTrue(result2.isEmpty())

        Log.i(TAG, "test11_Simple_encrypt_decrypt_OpenSSL_wrong_key() OUT")
    }

    @Test
    fun test12_Simple_encrypt_decrypt_Android_provider_short_key() {
        Log.i(TAG, "test12_Simple_encrypt_decrypt_Android_provider_short_key() IN")

        val crypto = Crypto()

        val key = "a" // short key
        val message = "b" // short message

        // Should fail as we need a 32 len key
        val result = crypto.encrypt(message, key)

        Assert.assertNotNull(result)
        Assert.assertTrue(result.isEmpty())

        Log.i(TAG, "test12_Simple_encrypt_decrypt_Android_provider_short_key() OUT")
    }

    @Test
    fun test13_Simple_encrypt_decrypt() {
        Log.i(TAG, "test13_Simple_encrypt_decrypt() IN")

        val crypto = Crypto(Crypto.CRYPTO_PROVIDER_OPENSSL)

        val key = "dhwidiqwduhiwudhiqwhdiqhwidhqwiudhiwhdqihdwqidwihq"
        val message = "The quick brown fox jumps over the lazy dog"

        val result = crypto.encrypt(message, key)

        Assert.assertNotNull(result)
        Assert.assertTrue(result.isNotEmpty())

        val crypto2 = Crypto(Crypto.CRYPTO_PROVIDER_OPENSSL)

        val decryptedMessage = crypto2.decrypt(result, key)

        Assert.assertNotNull(decryptedMessage)
        Assert.assertTrue(decryptedMessage == message)

        Log.i(TAG, "test13_Simple_encrypt_decrypt() OUT")
    }

    @Test
    fun test14_getRandomValue_verify_unique_values_generated() {
        Log.i(TAG, "test14_getRandomValue_verify_unique_values_generated() IN")

        val crypto = Crypto()
        val size = 10

        val byteArr1 = crypto.getRandomValue(size)

        Assert.assertNotNull(byteArr1)
        Assert.assertTrue(byteArr1.isNotEmpty())
        Assert.assertTrue(byteArr1.size == 10)

        val byteArr2 = crypto.getRandomValue(size)

        Assert.assertNotNull(byteArr2)
        Assert.assertTrue(byteArr2.isNotEmpty())
        Assert.assertTrue(byteArr2.size == 10)

        // Check that value are different
        Assert.assertFalse(byteArr1.contentEquals(byteArr2))

        Log.i(TAG, "test14_getRandomValue_verify_unique_values_generated() OUT")
    }

    companion object {
        private val TAG: String = CryptoTests::class.java.simpleName

        private const val SECRET_KET_TEST_A = "Key1"
        private const val SECRET_KET_TEST_B = "Key2"
        private const val SECRET_KET_TEST_C = "Key3"
    }

}
