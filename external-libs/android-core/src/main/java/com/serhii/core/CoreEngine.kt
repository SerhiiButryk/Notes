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
import com.serhii.core.security.impl.crypto.CryptoOpenssl
import java.lang.IllegalArgumentException

internal object CoreEngine : Components {

    private const val RUNTIME_LIBRARY = "core"
    private const val TAG = "CE"

    override fun configure(hash: Hash) {
        Log.info(TAG, "configure(), HH $hash")
        hash.setGenerator(HashAlgorithms())
    }

    override fun configure(cipher: Cipher) {
        Log.info(TAG, "configure(), CC $cipher")
        cipher.setCryptoProvider(SecureStore())
    }

    override fun configure(cipher: Cipher, provider: String) {
        Log.info(TAG, "configure(), CC1 $cipher : $provider")
        if (provider == Cipher.CRYPTO_PROVIDER_ANDROID) {
            cipher.setCryptoProvider(SecureStore())
        } else if (provider == Cipher.CRYPTO_PROVIDER_OPENSSL) {
            cipher.setCryptoProvider(CryptoOpenssl())
        } else {
            throw IllegalArgumentException("Unknown crypto provider is passed")
        }
    }

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