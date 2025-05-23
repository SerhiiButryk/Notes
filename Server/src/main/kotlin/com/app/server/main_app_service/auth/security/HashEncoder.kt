package com.app.server.main_app_service.auth.security

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class HashEncoder {

    private val crypto = BCryptPasswordEncoder()

    fun encode(rawPassword: String): String {
        return crypto.encode(rawPassword)
    }

    fun verify(rawPassword: String, encodedPassword: String): Boolean {
        return crypto.matches(rawPassword, encodedPassword)
    }
}