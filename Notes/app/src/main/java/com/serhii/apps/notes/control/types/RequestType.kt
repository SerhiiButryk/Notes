/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.control.types

enum class RequestType(var description: String, var type: Int) {
    REQ_AUTHORIZE("REQ_AUTHORIZE", 1), REQ_REGISTER(
        "REQ_REGISTER",
        2
    ),
    REQ_BIOMETRIC_LOGIN("REQ_BIOMETRIC_LOGIN", 3);
}