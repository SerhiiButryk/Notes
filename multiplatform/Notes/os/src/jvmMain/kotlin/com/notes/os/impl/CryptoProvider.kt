package com.notes.os.impl

import api.security.CryptoOperations
import com.notes.os.impl.crypto.TinkCrypto
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class CryptoProvider : CryptoOperations() {
    private var provider = TinkCrypto

    private val mutex = Mutex()

    override suspend fun encrypt(message: String): String =
        mutex.withLock {
            provider.encrypt(message)
        }

    override suspend fun decrypt(message: String): String =
        mutex.withLock {
            provider.decrypt(message)
        }
}
