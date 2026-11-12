package com.notes.os.impl.crypto

import api.Platform
import com.google.crypto.tink.Aead
import com.google.crypto.tink.BinaryKeysetReader
import com.google.crypto.tink.BinaryKeysetWriter
import com.google.crypto.tink.CleartextKeysetHandle
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.aead.AeadConfig
import java.io.File
import java.io.FileOutputStream

object TinkCrypto {
    private val keyFile = File(Platform().getCacheDir(), "datafile.dat")

    fun getCryptoHandle(): Aead {
        // 1. Initialize Tink AEAD config
        AeadConfig.register()

        // 2. Load or generate a KeysetHandle
        val keysetHandle: KeysetHandle =
            if (keyFile.exists()) {
                // TODO: In a production app, decrypt this keyset via
                //  OS KeyStore/Keyring (e.g., java KeyStore or SecretService)
                // And use 'readNoSecret' instead of 'CleartextKeysetHandle'
//            KeysetHandle.readNoSecret(keyFile.readBytes())

                CleartextKeysetHandle.read(
                    BinaryKeysetReader.withBytes(keyFile.readBytes()),
                )
            } else {
                val handle = KeysetHandle.generateNew(KeyTemplates.get("AES256_GCM"))

                FileOutputStream(keyFile).use { outputStream ->
                    // Write keyset using BinaryKeysetWriter
                    CleartextKeysetHandle.write(
                        handle,
                        BinaryKeysetWriter.withOutputStream(outputStream),
                    )
                }

                handle
            }

        // 3. Get the AEAD primitive
        return keysetHandle.getPrimitive(Aead::class.java)
    }

    fun encrypt(input: String): String {
        val aead = getCryptoHandle()

        val plaintextBytes = input.toByteArray(Charsets.UTF_8)

        val ciphertext: ByteArray = aead.encrypt(plaintextBytes, null)

        return String(Platform().base64.encode(ciphertext))
    }

    fun decrypt(input: String): String {
        val aead = getCryptoHandle()

        val ciphertext = Platform().base64.decode(input)

        val decryptedBytes: ByteArray = aead.decrypt(ciphertext, null)

        return String(decryptedBytes, Charsets.UTF_8)
    }
}
