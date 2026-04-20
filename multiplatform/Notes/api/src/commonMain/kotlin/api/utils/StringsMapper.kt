package api.utils

import api.auth.AuthResult
import api.auth.AuthResult.Companion.emailOrPassEmptyError
import api.auth.AuthResult.Companion.loginFailed
import api.auth.AuthResult.Companion.passwordEmptyOrNotMatchingError

fun getErrorTitleAndMessage(authResult: AuthResult): Pair<String, String> {
    var title = "Sorry, can't finish this operation."
    var subtitle = "Please, try again."

    if (authResult.status == passwordEmptyOrNotMatchingError) {
        title = "Sorry, registration has failed."
        subtitle = "Your passwords do not match or email is empty. Please, try again."
    }

    if (authResult.status == emailOrPassEmptyError) {
        title = "Sorry, login has failed."
        subtitle = "Your password or email is empty. Please, try again."
    }

    if (authResult.status == loginFailed) {
        title = "Sorry, login has failed."
        subtitle = "Please, try again."
    }

    return Pair(first = title, second = subtitle)
}

fun getVerifyTitleAndMessage(emailSent: Boolean, userEmail: String): Pair<String, String> {

    val title =
        if (emailSent) {
            "Verification email has been sent to you. " +
                    "Please, check your $userEmail email."
        } else {
            "We failed to send verification email to you. Please, retry later."
        }

    val subtitle =
        "Look for email with 'Verify your email for fancynotesdevtest' title. " +
                "If you don't see such email check 'Spam' folder."

    return Pair(first = title, second = subtitle)
}

fun getTitleAndMessage(
    registerUI: Boolean = false,
    loginUI: Boolean = false,
    passwordChangeUI: Boolean = false,
    authToConfirm: Boolean = false,
): Pair<String, String> {

    val title =
        if (authToConfirm) {
            "Reenter your credentials"
        } else if (passwordChangeUI) {
            "Set new password"
        } else if (registerUI) {
            "Create a user"
        } else if (loginUI) {
            "Welcome again !"
        } else {
            ""
        }

    val subtitle =
        if (authToConfirm) {
            "You are required to confirm your password"
        } else if (passwordChangeUI) {
            "Please, set a new strong password"
        } else if (registerUI) {
            "Set your user email and a password to access this application"
        } else if (loginUI) {
            "Sign in using your email and password"
        } else {
            ""
        }

    return Pair(first = title, second = subtitle)
}
