/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.security.impl.crypto

import com.serhii.core.security.Crypto

data class Result(
                  // Can contain IV data part
                  val message: String = "",
                  val iv: String = "",
                  val error: CryptoError = CryptoError.UNKNOWN,
                  val hasIV: Boolean = false) {

    // A message without IV data part
    val realMessage = if (hasIV && message.isNotEmpty())
        message.substring(Crypto.IV_SIZE) else message

    val errorOk: Boolean
        get() = error === CryptoError.OK

    override fun toString(): String {
        return ""
    }

    // Implemented for usage in Kotlin collections
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Result

        if (message != other.message) return false
        if (!iv.contentEquals(other.iv)) return false
        if (error != other.error) return false
        if (errorOk != other.errorOk) return false

        return true
    }

    // Implemented for usage in Kotlin collections
    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + iv.hashCode()
        result = 31 * result + error.hashCode()
        return result
    }

}