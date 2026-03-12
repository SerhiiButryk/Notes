package com.notes.os.impl

data class Result(
    // Can contain IV data part
    val message: String = "",
    val iv: String = "",
    val error: CryptoKeystore.CryptoError = CryptoKeystore.CryptoError.UNKNOWN,
    val hasIV: Boolean = false,
) {
    // A message without IV data part
    val realMessage =
        if (hasIV && message.isNotEmpty()) {
            message.substring(CryptoKeystore.IV_SIZE)
        } else {
            message
        }

    val success: Boolean
        get() = error === CryptoKeystore.CryptoError.OK

    override fun toString(): String = ""
}
