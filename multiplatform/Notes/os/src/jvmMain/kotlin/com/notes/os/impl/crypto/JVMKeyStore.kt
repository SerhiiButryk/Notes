package com.notes.os.impl.crypto

import api.Platform
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Class manages file based key store which is encrypted using provided master key
 * TODO: This class is unsued but left for test comparison
 */
class JVMKeyStore(
    private val masterPassword: CharArray,
) {
    private val keyStore: KeyStore = KeyStore.getInstance("PKCS12")
    private val keyStoreFile: File
    private val keyStoreFileName = "datastore.p12"
    private val secretKeyAlias = "DEFAULT_SEC_KEY_9134"
    private val TRANSFORMATION = "AES/GCM/NoPadding"
    private val IV_LENGTH_BYTES = 12
    private val TAG_LENGTH_BITS = 128

    init {
        val cacheDir = Platform().getCacheDir()
        keyStoreFile = File(cacheDir, keyStoreFileName)
    }

    fun init(mustCreateKeyStore: Boolean = false) {
        if (mustCreateKeyStore) {
            Platform().logger.logi("init() creating...")
            if (keyStoreFile.isFile && keyStoreFile.exists()) keyStoreFile.delete()
            // Create a brand new, empty KeyStore file
            keyStore.load(null, masterPassword)
            saveToFile()
        } else {
            Platform().logger.logi("init() loading...")
            ensureKeyStoreLoaded()
        }
    }

    /**
     * Ensures keystore is created and loaded
     */
    private fun ensureKeyStoreLoaded() {
        if (keyStoreFile.exists()) {
            Platform().logger.logi("ensureKeyStoreLoaded() exists !!!")
            // Load the existing KeyStore file
            FileInputStream(keyStoreFile).use { fis ->
                keyStore.load(fis, masterPassword)
            }
        } else {
            throw IllegalStateException("Expected a file")
        }
    }

    /**
     * Persist any in-memory changes back down to the hard drive
     */
    private fun saveToFile() {
        if (!keyStoreFile.exists()) {
            try {
                keyStoreFile.createNewFile()
            } catch (e: Exception) {
                Platform().logger.loge("saveToFile() can't create a file")
            }
        }
        FileOutputStream(keyStoreFile).use { fos ->
            keyStore.store(fos, masterPassword)
        }
    }

    /**
     * Encrypts plain text using AES-GCM.
     * Returns a combined byte array containing [IV (12 bytes) + Ciphertext + Auth Tag]
     */
    fun encrypt(input: String): ByteArray {
        Platform().logger.logi("encrypt()")

        val cipher = Cipher.getInstance(TRANSFORMATION)

        // 1. Generate a totally unique, random 12-byte IV
        val iv = ByteArray(IV_LENGTH_BYTES)
        SecureRandom().nextBytes(iv)

        val secretKey = getSecret()

        // 2. Initialize Cipher for encryption
        val parameterSpec = GCMParameterSpec(TAG_LENGTH_BITS, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec)

        // 3. Encrypt the data
        val plainTextBytes = input.toByteArray(Charsets.UTF_8)
        val cipherText = cipher.doFinal(plainTextBytes)

        // 4. Combine IV and Ciphertext into a single package for easy storage
        val combinedPackage = ByteArray(iv.size + cipherText.size)
        System.arraycopy(iv, 0, combinedPackage, 0, iv.size)
        System.arraycopy(cipherText, 0, combinedPackage, iv.size, cipherText.size)

        return combinedPackage
    }

    /**
     * Decrypts a combined byte array [IV + Ciphertext + Auth Tag] back to plain text.
     */
    fun decrypt(combinedPackage: ByteArray): String {
        Platform().logger.logi("decrypt()")

        val cipher = Cipher.getInstance(TRANSFORMATION)

        val secretKey = getSecret()

        // 1. Extract the 12-byte IV from the front of the package
        val iv = ByteArray(IV_LENGTH_BYTES)
        System.arraycopy(combinedPackage, 0, iv, 0, iv.size)

        // 2. Extract the remaining ciphertext bytes
        val cipherTextSize = combinedPackage.size - IV_LENGTH_BYTES
        val cipherText = ByteArray(cipherTextSize)
        System.arraycopy(combinedPackage, IV_LENGTH_BYTES, cipherText, 0, cipherTextSize)

        // 3. Initialize Cipher for decryption using the extracted IV
        val parameterSpec = GCMParameterSpec(TAG_LENGTH_BITS, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec)

        // 4. Decrypt and verify integrity (throws AEADBadTagException if altered!)
        val decryptedBytes = cipher.doFinal(cipherText)

        return String(decryptedBytes, Charsets.UTF_8)
    }

    /**
     * Get and gen a secret key if needed
     */
    private fun getSecret(): SecretKey {
        if (!keyStore.containsAlias(secretKeyAlias)) {
            Platform().logger.logi("getSecret() no secret, creating...")
            // Create
            val newSecret = generateAESGCMKey()

            // Wrap the key in a ProtectedEntry wrapper
            val protectionParameter = KeyStore.PasswordProtection(masterPassword)
            val secretKeyEntry = KeyStore.SecretKeyEntry(newSecret)

            keyStore.setEntry(secretKeyAlias, secretKeyEntry, protectionParameter)
            saveToFile() // Write changes out immediately
            Platform().logger.logi("getSecret() created")
        }

        val protectionParameter = KeyStore.PasswordProtection(masterPassword)
        val entry =
            keyStore.getEntry(secretKeyAlias, protectionParameter) as? KeyStore.SecretKeyEntry

        val secretKey = entry?.secretKey ?: throw IllegalStateException("No secret key")
        return secretKey
    }

    /**
     * Generates a secure 256-bit AES key suitable for GCM mode.
     */
    private fun generateAESGCMKey(): SecretKey {
        val keyGen = KeyGenerator.getInstance("AES")
        val secureRandom = SecureRandom() // Uses system-native entropy sources
        keyGen.init(256, secureRandom) // 256-bit strength is standard for high security
        return keyGen.generateKey()
    }

    /**
     * Delete an entry from the keystore
     */
    fun deleteSecret(alias: String) {
        if (keyStore.containsAlias(alias)) {
            keyStore.deleteEntry(alias)
            saveToFile()
        }
    }

    fun onDestroy() {
        keyStoreFile.delete()
    }

    fun testOnly_getKeystoreFile(): File = keyStoreFile
}
