package com.notes.os.impl

import api.Platform
import api.security.CryptoOperations
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class CryptoProvider : CryptoOperations() {

    private var provider: CryptoKeyStore? = null

    private val mutex = Mutex()

    override suspend fun encrypt(message: String): String =
        mutex.withLock {
            val bytes = provider!!.encrypt(message)
            String(Platform().base64.encode(bytes))
        }

    override suspend fun decrypt(message: String): String =
        mutex.withLock {
            val bytes = Platform().base64.decode(message)
            provider!!.decrypt(bytes)
        }

    override suspend fun onAuthCompleted(
        password: String,
        uid: String,
        force: Boolean
    ) {
        // At this moment we can create keystore
        if (provider == null) {
            val bytes = genDerivedKeyInter(password, uid)
            val encoded = String(Platform().base64.encode(bytes))
            provider = CryptoKeyStore(masterPassword = encoded.toCharArray())
        }
        // Will ensure that derived key is properly created
        super.onAuthCompleted(password, uid, force)
    }

    override fun onDestroy() {
        provider?.onDestroy()
    }
}

/**
 * Class manages file based key store which is encrypted using provided master key
 */
class CryptoKeyStore(
    private val masterPassword: CharArray
) {

    private val keyStore: KeyStore = KeyStore.getInstance("PKCS12")
    private val keyStoreFile: File
    private val secretKeyAlias = "DEFAULT_SEC_KEY_9134"
    private val TRANSFORMATION = "AES/GCM/NoPadding"
    private val IV_LENGTH_BYTES = 12
    private val TAG_LENGTH_BITS = 128

    init {
        val userHome = System.getProperty("user.home")
        val appDir = File(userHome, ".notes").apply { mkdirs() }
        keyStoreFile = File(appDir, "datastore.p12")
        ensureKeyStoreLoaded()
    }

    /**
     * Ensures keystore is created and loaded
     */
    fun ensureKeyStoreLoaded() {
        if (keyStoreFile.exists()) {
            Platform().logger.logi("ensureKeyStoreLoaded() exists !!!")
            // Load the existing KeyStore file
            FileInputStream(keyStoreFile).use { fis ->
                keyStore.load(fis, masterPassword)
            }
        } else {
            Platform().logger.logi("ensureKeyStoreLoaded() doesn't exist, creating...")
            // Create a brand new, empty KeyStore file
            keyStore.load(null, masterPassword)
            saveToFile()
        }
    }

    /**
     * Persist any in-memory changes back down to the hard drive
     */
    private fun saveToFile() {
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