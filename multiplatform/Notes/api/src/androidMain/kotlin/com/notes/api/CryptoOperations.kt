package com.notes.api

interface CryptoOperations {
    fun encrypt(message: String): String

    fun decrypt(message: String): String
}
