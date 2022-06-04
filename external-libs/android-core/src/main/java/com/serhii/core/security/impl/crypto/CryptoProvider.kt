/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.security.impl.crypto

/**
 * Interface for provider cryptography operations
 */
interface CryptoProvider : CryptoSymmetric {
    fun selectKey(key: String)
    fun createKey(key: String, timeOutSeconds: Int, authRequired: Boolean)
}