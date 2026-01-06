package com.notes.api

interface StorageOperations {
    suspend fun save(
        value: String,
        key: String,
    ): Boolean

    suspend fun get(key: String): String
}
