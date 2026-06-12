package com.notes.os.impl

import api.security.CryptoOperations
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class CryptoProvider : CryptoOperations() {

    private val provider = CryptoKeystore()

    private val protectKeyStore = Mutex()

    // NOTE: This call is not thread safe because KeyStore is not thread safe
    // That's why we use mutex
    override suspend fun encrypt(message: String): String =
        protectKeyStore.withLock { provider.encrypt(message).message }

    // NOTE: This call is not thread safe because KeyStore is not thread safe
    // That's why we use mutex
    override suspend fun decrypt(message: String): String =
        protectKeyStore.withLock { provider.decrypt(message).message }

    suspend fun testOnly_genDerivedKey(password: String, uid: String) = genDerivedKey(password, uid)

    suspend fun testOnly_getKey() = getKey()
}