package com.notes.app.security

import com.notes.app.security.CryptoProvider.CryptoError

data class Result(
    // Can contain IV data part
    val message: String = "",
    val iv: String = "",
    val error: CryptoError = CryptoError.UNKNOWN,
    val hasIV: Boolean = false) {

    // A message without IV data part
    val realMessage = if (hasIV && message.isNotEmpty())
        message.substring(CryptoProvider.IV_SIZE) else message

    val success: Boolean
        get() = error === CryptoError.OK

    override fun toString(): String {
        return ""
    }

}