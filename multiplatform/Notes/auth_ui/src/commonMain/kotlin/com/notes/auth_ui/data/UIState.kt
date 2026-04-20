package com.notes.auth_ui.data

import androidx.compose.runtime.Stable
import api.utils.getTitleAndMessage
import api.utils.getVerifyTitleAndMessage
import kotlinx.coroutines.flow.MutableStateFlow

open class UIState(
    val title: String = "",
    val subtitle: String = ""
)

// This annotation could be redundant as
// the class is already stable, because all properties are stable.
// However, keep it for clarity.
@Stable
class LoginUIState(
    val email: String = "",
    val password: String = "",
    val hasFocus: Boolean = false,
    val showProgress: Boolean = false,
    val uiForced: Boolean = true,
    val authToConfirm: Boolean = false,
) : UIState(
    title = getTitleAndMessage(loginUI = true, authToConfirm = authToConfirm).first,
    subtitle = getTitleAndMessage(loginUI = true, authToConfirm = authToConfirm).second
)

fun <T> MutableStateFlow<T>.copyLoginUIState(showProgress: Boolean): UIState? {
    val old = (value as? LoginUIState)
    if (old != null) {
        return LoginUIState(
            email = old.email,
            password = old.password,
            hasFocus = old.hasFocus,
            showProgress = showProgress,
            uiForced = old.uiForced,
            authToConfirm = old.authToConfirm
        )
    }
    return null
}

// This annotation could be redundant as
// the class is already stable, because all properties are stable.
// However, keep it for clarity.
@Stable
class RegisterUIState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val hasFocus: Boolean = false,
    val showChangePassword: Boolean = true,
) : UIState(
    title = getTitleAndMessage(registerUI = true, passwordChangeUI =  showChangePassword).first,
    subtitle = getTitleAndMessage(registerUI = true, passwordChangeUI =  showChangePassword).second
)

// This annotation could be redundant as
// the class is already stable, because all properties are stable.
// However, keep it for clarity.
@Stable
class DialogState(
    val title: String,
    val subtitle: String,
    val onConfirm: (() -> Unit)? = null
)

// This annotation could be redundant as
// the class is already stable, because all properties are stable.
// However, keep it for clarity.
@Stable
class VerificationUIState(
    val emailVerificationSent: Boolean = false,
    val email: String = "",
) : UIState(
    title = getVerifyTitleAndMessage(emailVerificationSent, email).first,
    subtitle = getVerifyTitleAndMessage(emailVerificationSent, email).second
)
