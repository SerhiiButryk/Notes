package com.notes.auth_ui

import com.notes.api.AuthResult
import com.notes.api.AuthResult.Companion.emailOrPassEmptyError
import com.notes.api.AuthResult.Companion.loginFailed
import com.notes.api.AuthResult.Companion.passwordEmptyOrNotMatchingError

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
