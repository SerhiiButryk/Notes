/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.core

import com.serhii.core.base.Components
import com.serhii.core.security.Hash
import com.serhii.core.security.impl.hash.HashAlgorithms
import com.serhii.core.security.impl.crypto.SecureStore
import com.serhii.core.log.Log
import com.serhii.core.security.Cipher
import com.serhii.core.security.impl.crypto.Openssl
import com.serhii.core.security.impl.crypto.CryptoProvider
import com.serhii.core.security.impl.hash.HashGenerator
import java.lang.IllegalArgumentException

/**
 * Class which initializes library components configuration.
 */
internal object CoreEngine : Components {

    private const val RUNTIME_LIBRARY = "core"
    private const val TAG = "CE"

    private val providerOpenSSL = Openssl()
    private val providerSecureStore = SecureStore()
    private val hashProvider = HashAlgorithms()

    override fun configure(hash: Hash) : HashGenerator {
        Log.info(TAG, "configure(), HH $hash")
        return hashProvider;
    }

    override fun configure(cipher: Cipher) : CryptoProvider {
        Log.info(TAG, "configure(), CC $cipher")
        return providerSecureStore
    }

    override fun configure(cipher: Cipher, provider: String) : CryptoProvider {
        Log.info(TAG, "configure(), CC1 $cipher : $provider")
        return when (provider) {
            Cipher.CRYPTO_PROVIDER_ANDROID -> {
                providerSecureStore
            }
            Cipher.CRYPTO_PROVIDER_OPENSSL -> {
                providerOpenSSL
            }
            else -> {
                throw IllegalArgumentException("Unknown crypto provider is passed")
            }
        }
    }

    fun getOpenSSLProvider(): Openssl = providerOpenSSL

    fun loadNativeLibrary() {
        Log.info(TAG, "loadNativeLibrary() IN")
        try {
            System.loadLibrary(RUNTIME_LIBRARY)
        } catch (e: Exception) {
            Log.error(TAG, "error: $e")
        }
        Log.info(TAG, "loadNativeLibrary() OUT")
    }

}