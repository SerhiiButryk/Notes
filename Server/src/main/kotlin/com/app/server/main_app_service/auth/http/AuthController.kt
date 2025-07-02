package com.app.server.main_app_service.auth.http

import com.app.server.main_app_service.auth.AuthService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// Handles the next requests:
// POST http://localhost:port/auth/register
// POST http://localhost:port/auth/login
// POST http://localhost:port/auth/refresh

private object Path {
    const val AUTH = "auth"
    const val REGISTER = "/register"
    const val LOGIN = "/login"
    const val REFRESH_TOKEN = "/refresh"
}

@RestController
@RequestMapping(Path.AUTH)
class AuthController(
    private val authService: AuthService
) {

    @PostMapping(Path.REGISTER)
    fun register(
        @Valid @RequestBody body: AuthRequest
    ) : ResponseEntity<Any> {
        authService.register(body.email, body.password)
        return ResponseEntity.ok().build()
    }

    @PostMapping(Path.LOGIN)
    fun login(
        @RequestBody body: AuthRequest
    ) : TokensResponse {
        return authService.login(body.email, body.password)
    }

    @PostMapping(Path.REFRESH_TOKEN)
    fun refreshToken(
        @RequestBody body: RefreshTokenRequest
    ) : TokensResponse {
        return authService.refresh(body.refreshToken)
    }

}