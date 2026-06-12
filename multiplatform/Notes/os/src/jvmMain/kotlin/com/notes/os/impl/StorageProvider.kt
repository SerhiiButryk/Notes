package com.notes.os.impl

import api.data.StorageOperations

class StorageProvider : StorageOperations {

    private val map = mutableMapOf<String, String>()

    override suspend fun save(value: String, key: String): Boolean {
        map[key] = value
        return true
    }

    override suspend fun get(key: String): String {
        return map[key] ?: ""
    }

    override suspend fun clearAll() {
        map.clear()
    }

    override fun getCacheDir(): String {
        throw IllegalStateException("Not implemented")
    }

    override fun getRootFilesDir(): String {
        return ""
    }

}