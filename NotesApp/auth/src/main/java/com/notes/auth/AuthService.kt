package com.notes.auth

object AuthService {

    private const val PASSWORD_SALT_KEY = "password_salt_key"

    fun register(pass: String, confirmPass: String, email: String) {

        val salt = generateSalt()
        updateSalt(salt)

        if (!pass.equals(confirmPass)) {
            // TODO throw error and handle on upper layer
        }

        val key1 = generatePDKey(pass, salt)
        val key2 = generatePDKey(confirmPass, salt)

        // TODO Execute request
        // TODO Get response
        // TODO Update app state

    }

    fun login(pass: String, email: String) {

        val salt = getSalt()
        val key = generatePDKey(pass, salt)

        // TODO Execute request
        // TODO Get response
        // TODO Update app state
    }

    fun refreshToken() {

    }

    private fun updateSalt(salt: ByteArray) {
        val result = AuthAPIBridge.base64.encode(salt)
        AuthAPIBridge.storage.save(result, PASSWORD_SALT_KEY)
    }

    private fun getSalt(): ByteArray {
        val saltString = AuthAPIBridge.storage.get(PASSWORD_SALT_KEY)
        return AuthAPIBridge.base64.decode(saltString)
    }

}