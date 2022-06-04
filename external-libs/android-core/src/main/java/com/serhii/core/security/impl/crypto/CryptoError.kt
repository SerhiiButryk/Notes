/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.security.impl.crypto

enum class CryptoError(val code: Int, val errorName: String) {
    OK(1, "NO_ERRORS"),
    USER_NOT_AUTHORIZED(2, "USER_NOT_AUTHORIZED"),
    UNKNOWN(3, "UNKNOWN");
}