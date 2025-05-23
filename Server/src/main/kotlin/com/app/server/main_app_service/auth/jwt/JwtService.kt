package com.app.server.main_app_service.auth.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.ACCESS_TOKEN
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.REFRESH_TOKEN
import org.springframework.stereotype.Service
import java.util.Base64
import java.util.Date

@Service
class JwtService(
    @Value("\${jwt.secret}") private val secret: String
) {

    private val secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret))

    private val accessTokenValidityPeriodMs = 15L * 60 * 1000L
    val refreshTokenValidityPeriodMs = 15L * 60 * 1000L

    private val headerPrefix = "Bearer "

    fun genToken(
        user: String,
        type: String,
        periodMs: Long
    ): String {
        val now = Date()
        val expiry = Date(now.time + periodMs)
        return Jwts
            .builder()
            .subject(user)
            .claim("type", type)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact()
    }

    fun genAccessToken(user: String): String {
        return genToken(user, ACCESS_TOKEN, accessTokenValidityPeriodMs)
    }

    fun genRefreshToken(user: String): String {
        return genToken(user, REFRESH_TOKEN, refreshTokenValidityPeriodMs)
    }

    fun isValidToken(type: String, token: String): Boolean {
        val claims = parseClaims(token) ?: return false
        val tokenType = claims["type"] as? String ?: return false
        return tokenType == type
    }

    fun getUserIdByToken(token: String): String {
        val claims = parseClaims(token) ?: throw IllegalStateException("Invalid token")
        return claims.subject
    }

    private fun parseClaims(token: String): Claims? {
        val rawToken = if (token.startsWith(headerPrefix)) {
            token.removePrefix(headerPrefix)
        } else token
        return try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(rawToken)
                .payload
        } catch (e: Exception) {
            null
        }
    }

    fun hasToken(header: String?): Boolean {
        return (header != null && header.startsWith(headerPrefix))
    }

}