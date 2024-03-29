/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.security.impl.crypto

data class Result(val message: String = "",
                  val iv: ByteArray = ByteArray(0),
                  val error: CryptoError = CryptoError.UNKNOWN) {

    val isResultAvailable: Boolean
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
        if (isResultAvailable != other.isResultAvailable) return false

        return true
    }

    // Implemented for usage in Kotlin collections
    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + iv.contentHashCode()
        result = 31 * result + error.hashCode()
        result = 31 * result + isResultAvailable.hashCode()
        return result
    }

}