/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.data_model

import com.serhii.apps.notes.control.auth.types.UIRequestType
import javax.crypto.Cipher

data class AuthModel(
    var email: String = "", var password: String = "",
    var confirmPassword: String = "", var authType: UIRequestType = UIRequestType.UN_SET
) {
    var cipher: Cipher? = null
}

fun createModel(email: String, password: String, type: UIRequestType): AuthModel {
    val authModel = AuthModel(
        email,
        password,
        "",
        type
    )
    return authModel
}

fun createModel(
    email: String,
    password: String,
    confirmPassword: String,
    type: UIRequestType
): AuthModel {
    val authModel = AuthModel(
        email,
        password,
        confirmPassword,
        type
    )
    return authModel
}

fun createModel(cipher: Cipher, type: UIRequestType): AuthModel {
    val model = AuthModel("", "", "", type)
    model.cipher = cipher
    return model
}