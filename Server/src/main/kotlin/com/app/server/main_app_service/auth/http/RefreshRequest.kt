package com.app.server.main_app_service.auth.http

data class RefreshTokenRequest(
    val refreshToken: String
)