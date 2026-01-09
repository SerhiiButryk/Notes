package api.auth

@ConsistentCopyVisibility
data class AuthResult private constructor(
    val email: String = "",
    val status: Int = 0,
    val statusCode: Int = 0,
    val refreshToken: String = "",
    val accessToken: String = "",
) {
    fun isSuccess(): Boolean = status > 0

    fun isEmailVerificationPassed(): Boolean = status == verificationSentOk
    fun isEmailVerificationFailed(): Boolean = status == verificationSentError

    companion object {

        // Encapsulating errors so call side don't need to depend on them

        internal const val passwordEmptyOrNotMatchingError: Int = -1
        internal const val registrationFailed: Int = -2
        internal const val emailOrPassEmptyError: Int = -3
        internal const val loginFailed: Int = -4
        internal const val verificationSentError: Int = -6

        internal const val registrationSuccess: Int = 1
        internal const val loginSuccess: Int = 2
        internal const val verificationSentOk: Int = 3

        fun passwordEmptyOrNotMatching(email: String): AuthResult =
            AuthResult(email, passwordEmptyOrNotMatchingError)

        fun registrationSuccess(email: String): AuthResult =
            AuthResult(email = email, status = registrationSuccess)

        fun registrationFailed(
            email: String,
            statusCode: Int = 0,
        ): AuthResult =
            AuthResult(email = email, status = registrationFailed, statusCode = statusCode)

        fun emailOrPassEmpty(email: String): AuthResult =
            AuthResult(email = email, status = emailOrPassEmptyError)

        fun loginSuccess(): AuthResult = AuthResult(status = loginSuccess)

        fun loginFailed(): AuthResult = AuthResult(status = loginFailed)

        fun verificationSentFailed(email: String): AuthResult =
            AuthResult(email = email, status = verificationSentError)

        fun verificationSentSuccess(email: String): AuthResult =
            AuthResult(email = email, status = verificationSentOk)
    }

}