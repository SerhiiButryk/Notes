/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.security.impl.crypto

interface CryptoSymmetric {
    fun encryptSymmetric(message: String, inputIV: ByteArray, key: String? = null): Result
    fun decryptSymmetric(message: String, inputIV: ByteArray, key: String? = null): Result
}