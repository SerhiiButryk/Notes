package com.notes.api

@ConsistentCopyVisibility
data class AuthResult private constructor(
    val email: String = "",
    val status: Int = 0,
    val statusCode: Int = 0,
    val refreshToken: String = "",
    val accessToken: String = "",
) {
    fun isSuccess(): Boolean = status > 0

    companion object {
        // Encapsulating errors so call side don't need to depend on them

        const val passwordEmptyOrNotMatchingError: Int = -1
        const val registrationFailed: Int = -2
        const val emailOrPassEmptyError: Int = -3
        const val refreshTokenFailed: Int = -4
        const val loginFailed: Int = -5
        const val verificationSentError: Int = -6

        const val registrationSuccess: Int = 1
        const val loginSuccess: Int = 2
        const val refreshTokenSuccess: Int = 3
        const val verificationSentOk: Int = 4

        fun passwordEmptyOrNotMatching(email: String): AuthResult = AuthResult(email, passwordEmptyOrNotMatchingError)

        fun registrationSuccess(email: String): AuthResult = AuthResult(email = email, status = registrationSuccess)

        fun registrationFailed(
            email: String,
            statusCode: Int = 0,
        ): AuthResult = AuthResult(email = email, status = registrationFailed, statusCode = statusCode)

        fun emailOrPassEmpty(email: String): AuthResult = AuthResult(email = email, status = emailOrPassEmptyError)

        fun refreshTokenSuccess(
            refreshToken: String,
            accessToken: String,
        ): AuthResult = AuthResult(refreshToken = refreshToken, accessToken = accessToken, status = refreshTokenSuccess)

        fun refreshTokenFailed(statusCode: Int): AuthResult = AuthResult(statusCode = statusCode, status = refreshTokenFailed)

        fun loginSuccess(
            email: String,
            refreshToken: String,
            accessToken: String,
        ): AuthResult = AuthResult(email = email, refreshToken = refreshToken, accessToken = accessToken, status = loginSuccess)

        fun loginSuccess(): AuthResult = AuthResult(status = loginSuccess)

        fun loginFailed(
            email: String,
            statusCode: Int,
        ): AuthResult = AuthResult(email = email, statusCode = statusCode, status = loginFailed)

        fun loginFailed(): AuthResult = AuthResult(status = loginFailed)

        fun verificationSentFailed(email: String): AuthResult = AuthResult(email = email, status = verificationSentError)

        fun verificationSentSuccess(email: String): AuthResult = AuthResult(email = email, status = verificationSentOk)
    }
}
