package com.notes.app.data

import com.notes.interfaces.StorageOperations

class StorageProvider : StorageOperations {

    override fun save(value: String, key: String): Boolean {
//        TODO("Not yet implemented")
        return true
    }

    override fun get(key: String): String {
//        TODO("Not yet implemented")
        return ""
    }
}