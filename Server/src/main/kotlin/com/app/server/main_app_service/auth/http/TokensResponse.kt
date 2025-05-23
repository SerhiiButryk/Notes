package com.app.server.main_app_service.auth.http

data class TokensResponse(
    val refreshToken: String,
    val accessToken: String
)