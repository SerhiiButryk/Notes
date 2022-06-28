/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.base

import com.serhii.core.security.Cipher
import com.serhii.core.security.Hash
import com.serhii.core.security.impl.crypto.CryptoProvider
import com.serhii.core.security.impl.crypto.Result
import com.serhii.core.security.impl.hash.HashGenerator

/**
 * Initialization interface for library components
 */
internal interface Components {
    fun configure(hash: Hash) : HashGenerator
    fun configure(cipher: Cipher) : CryptoProvider
    fun configure(cipher: Cipher, provider: String) : CryptoProvider { return CryptoProviderStub() }
}

/**
 * No-op
 */
class CryptoProviderStub : CryptoProvider {
    override fun selectKey(key: String) {
    }

    override fun createKey(key: String, timeOutSeconds: Int, authRequired: Boolean) {
    }

    override fun encryptSymmetric(message: String, inputIV: ByteArray, key: String?): Result {
        return Result()
    }

    override fun decryptSymmetric(message: String, inputIV: ByteArray, key: String?): Result {
        return Result()
    }
}