package com.notes.auth.data

import com.notes.interfaces.PlatformAPIs

private const val PASSWORD_SALT_KEY = "password_salt_key"

suspend fun updateSalt(salt: ByteArray) {
    val result = PlatformAPIs.base64.encode(salt)
    PlatformAPIs.storage.save(result, PASSWORD_SALT_KEY)
}

suspend fun getSalt(): ByteArray {
    val saltString = PlatformAPIs.storage.get(PASSWORD_SALT_KEY)
    return PlatformAPIs.base64.decode(saltString)
}