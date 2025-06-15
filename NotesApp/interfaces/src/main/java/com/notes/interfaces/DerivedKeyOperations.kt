package com.notes.interfaces

interface DerivedKeyOperations {
    fun generatePDKey(input: String, salt: ByteArray): String
    fun generateSalt(): ByteArray
}