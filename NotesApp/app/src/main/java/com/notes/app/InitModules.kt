package com.notes.app

import android.util.Base64
import android.util.Log
import com.notes.auth.Base64Operations
import com.notes.auth.AuthAPIBridge
import com.notes.auth.StorageOperations

/**
 * Provide platform implementation for other layers
 */
fun initModules() {

    AuthAPIBridge.base64 = object : Base64Operations {

        override fun encode(byteArray: ByteArray): String {
            return Base64.encodeToString(byteArray, Base64.NO_WRAP)
        }

        override fun decode(token: String): ByteArray {
            return Base64.decode(token, Base64.NO_WRAP)
        }
    }

    AuthAPIBridge.storage = object : StorageOperations {

        override fun save(value: String, key: String): Boolean {
            TODO("Not yet implemented")
        }

        override fun get(key: String): String {
            TODO("Not yet implemented")
        }
    }

    Log.i("InitModules", "initModules: done")
}