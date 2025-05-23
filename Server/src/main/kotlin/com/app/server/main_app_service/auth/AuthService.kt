package com.app.server.main_app_service.auth

import com.app.server.main_app_service.auth.data.RefreshTokenRepository
import com.app.server.main_app_service.auth.data.UserRepository
import com.app.server.main_app_service.auth.data.RefreshToken
import com.app.server.main_app_service.auth.data.User
import com.app.server.main_app_service.auth.http.TokensResponse
import com.app.server.main_app_service.auth.security.HashEncoder
import com.app.server.main_app_service.auth.jwt.JwtService
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64

@Service
class AuthService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val hashEncoder: HashEncoder,
    private val refreshTokenRepository: RefreshTokenRepository
) {

    fun register(email: String, password: String): User {

        val user = userRepository.findByEmail(email.trim())
        if (user != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "A user with that email already exists.")
        }

        return userRepository.save(
            User(email, hashEncoder.encode(password))
        )
    }

    fun login(email: String, password: String): TokensResponse {

        val user = userRepository.findByEmail(email)
            ?: throw BadCredentialsException("User is not registered")

        if (!hashEncoder.verify(password, user.passwordHash))
            throw BadCredentialsException("Invalid credentials")

        val refreshToken = jwtService.genRefreshToken(user.id.toHexString())
        val accessToken = jwtService.genAccessToken(user.id.toHexString())

        storeRefreshToken(user.id, refreshToken)

        return TokensResponse(refreshToken = refreshToken, accessToken = accessToken)
    }

    @Transactional
    fun refresh(token: String): TokensResponse {

        if (!jwtService.isValidToken("refresh", token))
            throw ResponseStatusException(HttpStatusCode.valueOf(401), "Invalid refresh token")

        val userId = jwtService.getUserIdByToken(token)

        val user = userRepository.findById(ObjectId(userId)).orElseThrow {
            throw ResponseStatusException(HttpStatusCode.valueOf(401), "Invalid refresh token")
        }

        val encoded = hashToken(token)

        refreshTokenRepository.findByUserIdAndHashedToken(userId = user.id, hashedToken = encoded)
            ?: throw ResponseStatusException(HttpStatusCode.valueOf(401), "Refresh token is not recognized")

        refreshTokenRepository.deleteByUserIdAndHashedToken(userId = user.id, hashedToken = encoded)

        val refreshToken = jwtService.genRefreshToken(user.id.toHexString())
        val accessToken = jwtService.genAccessToken(user.id.toHexString())

        storeRefreshToken(user.id, refreshToken)

        return TokensResponse(refreshToken = refreshToken, accessToken = accessToken)
    }

    private fun storeRefreshToken(userId: ObjectId, token: String) {

        val encoded = hashToken(token)

        refreshTokenRepository.save(
            RefreshToken(
                userId = userId,
                expiresAt = Instant.now().plusMillis(jwtService.refreshTokenValidityPeriodMs),
                hashedToken = encoded
            )
        )
    }

    private fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return Base64.getEncoder().encodeToString(digest.digest(token.encodeToByteArray()))
    }

}