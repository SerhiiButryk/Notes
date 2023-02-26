/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.control.auth.types

enum class AuthResult(var typeId: Int, var typeName: String) {
    // Has to have non positive value
    EMPTY_FIELD(-1, "EMPTY_FIELD"),
    WRONG_PASSWORD(-2, "WRONG_PASSWORD"),
    ACCOUNT_INVALID(-3, "ACCOUNT_INVALID"),
    USER_NAME_EXISTS(-4, "USER_NAME_EXISTS"),
    PASSWORD_DIFFERS(-5, "PASSWORD_DIFFERS"),
    SPACE_CONTAIN(-6, "SPACE_CONTAIN"),
    UNLOCK_KEY_INVALID(-10, "UNLOCK_KEY_INVALID"),
    // User entered short password
    ALERT_DIALOG_TYPE_PASSWORD_IS_SHORT(-12, "ALERT_DIALOG_TYPE_PASSWORD_IS_SHORT"),
    // User entered email not in form of 'username@email.com'
    EMAIL_INVALID(-13, "EMAIL_INVALID");
}