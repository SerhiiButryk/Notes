package com.notes.interfaces

interface StorageOperations {
    fun save(value: String, key: String): Boolean
    fun get(key: String): String
}