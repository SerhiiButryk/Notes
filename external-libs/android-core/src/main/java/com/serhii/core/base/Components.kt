/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.base

import com.serhii.core.security.Crypto
import com.serhii.core.security.Hash
import com.serhii.core.security.impl.crypto.CryptoProvider
import com.serhii.core.security.impl.hash.HashGenerator
import java.lang.RuntimeException

/**
 * Initialization interface for library components
 */
internal interface Components {
    fun configure(hash: Hash) : HashGenerator
    fun configure(cipher: Crypto?) : CryptoProvider
    fun configure(cipher: Crypto?, provider: String) : CryptoProvider { throw RuntimeException("Illegal operation") }
}