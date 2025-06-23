package com.notes.auth

import com.notes.interfaces.PlatformAPIs

class AuthService {

    private val PASSWORD_SALT_KEY = "password_salt_key"
    private val serverClient = ServerClient()

    fun register(pass: String, confirmPass: String, email: String) {

        val salt = PlatformAPIs.derivedKey.generateSalt()
        updateSalt(salt)

        if (!pass.equals(confirmPass)) {
            // TODO throw error and handle on upper layer
        }

        val passEncoded = PlatformAPIs.derivedKey.generatePDKey(pass, salt)
        val confirmPassEncoded = PlatformAPIs.derivedKey.generatePDKey(confirmPass, salt)

        serverClient.request(email, passEncoded, confirmPassEncoded)

    }

    fun login(pass: String, email: String) {

        val salt = getSalt()
        val key = PlatformAPIs.derivedKey.generatePDKey(pass, salt)

        // TODO Execute request
        // TODO Get response
        // TODO Update app state
    }

    fun refreshToken() {

    }

    private fun updateSalt(salt: ByteArray) {
        val result = PlatformAPIs.base64.encode(salt)
        PlatformAPIs.storage.save(result, PASSWORD_SALT_KEY)
    }

    private fun getSalt(): ByteArray {
        val saltString = PlatformAPIs.storage.get(PASSWORD_SALT_KEY)
        return PlatformAPIs.base64.decode(saltString)
    }

}