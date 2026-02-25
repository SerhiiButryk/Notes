package com.notes.auth_ui.ui

import androidx.compose.runtime.Stable
import api.getVerifyTitleAndMessage

open class UIState

// This annotation could be redundant as
// the class is already stable, because all properties are stable.
// However, keep it for clarity.
@Stable
data class LoginUIState(
    val email: String = "",
    val password: String = "",
    val hasFocus: Boolean = false,
    val showProgress: Boolean = false,
    val uiForced: Boolean = true
) : UIState()

// This annotation could be redundant as
// the class is already stable, because all properties are stable.
// However, keep it for clarity.
@Stable
data class RegisterUIState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val hasFocus: Boolean = false,
) : UIState()

// This annotation could be redundant as
// the class is already stable, because all properties are stable.
// However, keep it for clarity.
@Stable
data class DialogState(
    val title: String,
    val subtitle: String,
)

// This annotation could be redundant as
// the class is already stable, because all properties are stable.
// However, keep it for clarity.
@Stable
data class VerificationUIState(
    val emailVerificationSent: Boolean = false,
    val email: String = "",
    val isEmailVerified: Boolean = false,
) : UIState() {
    val title =
        getVerifyTitleAndMessage(emailVerificationSent, email)
            .first
    val subtitle =
        getVerifyTitleAndMessage(emailVerificationSent, email)
            .second
}
