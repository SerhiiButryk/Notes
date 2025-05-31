package com.notes.auth

/**
 * Implemented by the platform to provide some platform specific operations for this module
 */
object AuthAPIBridge {
    lateinit var base64: Base64Operations
    lateinit var storage: StorageOperations
}

interface Base64Operations {
    fun encode(byteArray: ByteArray): String
    fun decode(token: String): ByteArray
}

interface StorageOperations {
    fun save(value: String, key: String): Boolean
    fun get(key: String): String
}
