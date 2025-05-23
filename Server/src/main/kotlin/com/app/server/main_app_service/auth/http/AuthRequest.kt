package com.app.server.main_app_service.auth.http

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern

data class AuthRequest(

    @field:Email(message = "Invalid email")
    val email: String,

    @field:Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{9,}$",
        message = "Password must be at least 9 characters")
    val password: String

)