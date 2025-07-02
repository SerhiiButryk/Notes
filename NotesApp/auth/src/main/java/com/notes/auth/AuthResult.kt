package com.notes.auth

data class AuthResult(
    val email: String = "",
    val status: Int = 0,
    val statusCode: Int = 0,
    val refreshToken: String = "",
    val accessToken: String = ""
) {

    fun isSuccess(): Boolean {
        return status > 0
    }

    companion object {

        // Encapsulating errors so call side don't need to depend on them

        private const val passwordEmptyOrNotMatchingError: Int = -1
        private const val registrationFailed: Int = -2
        private const val emailOrPassEmptyError: Int = -3
        private const val refreshTokenFailed: Int = -4
        private const val loginFailed: Int = -5

        private const val registrationSuccess: Int = 1
        private const val loginSuccess: Int = 2
        private const val refreshTokenSuccess: Int = 2

        fun passwordEmptyOrNotMatching(email: String): AuthResult {
            return AuthResult(email, passwordEmptyOrNotMatchingError)
        }

        fun registrationSuccess(email: String): AuthResult {
            return AuthResult(email = email, status = registrationSuccess)
        }

        fun registrationFailed(email: String, statusCode: Int): AuthResult {
            return AuthResult(email = email, status = registrationFailed, statusCode = statusCode)
        }

        fun emailOrPassEmpty(email: String): AuthResult {
            return AuthResult(email = email, status = emailOrPassEmptyError)
        }

        fun refreshTokenSuccess(refreshToken: String, accessToken: String): AuthResult {
            return AuthResult(refreshToken = refreshToken, accessToken = accessToken, status = refreshTokenSuccess)
        }

        fun refreshTokenFailed(statusCode: Int): AuthResult {
            return AuthResult(statusCode = statusCode, status = refreshTokenFailed)
        }

        fun loginSuccess(email: String, refreshToken: String, accessToken: String): AuthResult {
            return AuthResult(email = email, refreshToken = refreshToken, accessToken = accessToken, status = loginSuccess)
        }

        fun loginFailed(email: String, statusCode: Int): AuthResult {
            return AuthResult(email = email, statusCode = statusCode, status = loginFailed)
        }
    }

}



