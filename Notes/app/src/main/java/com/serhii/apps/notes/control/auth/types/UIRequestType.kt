/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.control.auth.types

enum class UIRequestType(val description: String, val type: Int) {

    REGISTRATION("REGISTRATION", 101),

    PASSWORD_LOGIN("PASSWORD_LOGIN", 102),

    BIOMETRIC_LOGIN_UI("BIOMETRIC_LOGIN", 104),

    WELCOME_UI("WELCOME_UI", 105),

    DIALOG_UI("SHOW_DIALOG", 106),

    LOGIN_UI("LOGIN_UI", 108),

    SETUP_BIOMETRICS_UI("SHOW_BIOMETRICS_UI", 109),

    FORGOT_PASSWORD_UI("FORGOT_PASSWORD_UI", 110),

    FORGOT_PASSWORD("FORGOT_PASSWORD", 111),

    UN_SET("UN_SET", 1);
}