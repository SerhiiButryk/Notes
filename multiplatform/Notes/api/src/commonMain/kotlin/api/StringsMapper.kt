package api

import api.auth.AuthResult
import api.auth.AuthResult.Companion.emailOrPassEmptyError
import api.auth.AuthResult.Companion.loginFailed
import api.auth.AuthResult.Companion.passwordEmptyOrNotMatchingError

fun getErrorTitleAndMessage(authResult: AuthResult): Pair<String, String> {
    var title = "Sorry, can't finish this operation."
    var message = "Please, try again."

    if (authResult.status == passwordEmptyOrNotMatchingError) {
        title = "Sorry, registration has failed."
        message = "Your passwords do not match or email is empty. Please, try again."
    }

    if (authResult.status == emailOrPassEmptyError) {
        title = "Sorry, login has failed."
        message = "Your password or email is empty. Please, try again."
    }

    if (authResult.status == loginFailed) {
        title = "Sorry, login has failed."
        message = "Please, try again."
    }

    return Pair(first = title, second = message)
}

fun getVerifyTitleAndMessage(emailSent: Boolean, userEmail: String): Pair<String, String> {

    val title =
        if (emailSent) {
            "Verification email has been sent to you. " +
                    "Please, check your $userEmail email."
        } else {
            "We failed to send verification email to you. Please, retry later."
        }

    val message =
        "Look for email with 'Verify your email for fancynotesdevtest' title. " +
                "If you don't see such email check 'Spam' folder."

    return Pair(first = title, second = message)
}
