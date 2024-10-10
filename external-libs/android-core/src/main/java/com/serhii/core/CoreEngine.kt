/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.core

import com.serhii.core.base.Components
import com.serhii.core.security.Hash
import com.serhii.core.security.impl.hash.HashAlgorithms
import com.serhii.core.security.impl.crypto.AndroidProvider
import com.serhii.core.log.Log
import com.serhii.core.security.Crypto
import com.serhii.core.security.impl.KeyMaster
import com.serhii.core.security.impl.crypto.Openssl
import com.serhii.core.security.impl.crypto.CryptoProvider
import com.serhii.core.security.impl.hash.HashGenerator
import java.lang.IllegalArgumentException

/**
 * Class which provides an access to library components and configurations.
 */
internal object CoreEngine : Components {

    private const val RUNTIME_LIBRARY = "core"
    private const val TAG = "CE"

    private val providerOpenSSL = Openssl()
    private val providerAndroid = AndroidProvider()
    private val hashProvider = HashAlgorithms()
    private val keyMaster = KeyMaster(providerOpenSSL)

    override fun configure(hash: Hash) : HashGenerator {
        Log.info(TAG, "configure(), HH $hash")
        return hashProvider;
    }

    override fun configure(cipher: Crypto?) : CryptoProvider {
        Log.info(TAG, "configure(), CC")
        return providerAndroid
    }

    override fun configure(cipher: Crypto?, provider: String) : CryptoProvider {
        Log.info(TAG, "configure(), CC1")
        return when (provider) {
            Crypto.CRYPTO_PROVIDER_ANDROID -> {
                providerAndroid
            }
            Crypto.CRYPTO_PROVIDER_OPENSSL -> {
                providerOpenSSL
            }
            else -> {
                throw IllegalArgumentException("Unknown crypto provider is passed")
            }
        }
    }

    fun getKeyMaster() = keyMaster

    fun loadNativeLibrary() {
        Log.info(TAG, "loadNativeLibrary() IN")
        try {
            System.loadLibrary(RUNTIME_LIBRARY)
        } catch (e: UnsatisfiedLinkError) {
            Log.error(TAG, "failed to load native library, error: $e")
        }
        Log.info(TAG, "loadNativeLibrary() OUT")
    }

}