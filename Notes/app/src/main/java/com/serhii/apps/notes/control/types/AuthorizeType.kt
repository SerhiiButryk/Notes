/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.control.types

enum class AuthorizeType(var description: String, var type: Int) {
    AUTH_REGISTRATION("AUTH_REGISTRATION", 101), AUTH_PASSWORD_LOGIN(
        "AUTH_PASSWORD_LOGIN",
        102
    ),
    AUTH_UNLOCK("AUTH_UNLOCK_LOGIN", 103), AUTH_BIOMETRIC_LOGIN(
        "AUTH_BIOMETRIC_LOGIN",
        104
    ),
    UN_SET("UN_SET", 1);
}