/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.data_model

import com.serhii.apps.notes.control.auth.types.AuthorizeType
import com.serhii.core.utils.GoodUtils
import javax.crypto.Cipher

data class AuthModel(val email: String = "", var password: String = "",
                     var confirmPassword: String = "", val authType: AuthorizeType = AuthorizeType.UN_SET) {
    var cipher: Cipher? = null
}

fun createModel(email: String, password: String, type: AuthorizeType): AuthModel {
    val authModel = AuthModel(
        email,
        password,
        "",
        type
    )
    return authModel
}

fun createModel(email: String, password: String, confirmPassword: String, type: AuthorizeType): AuthModel {
    val authModel = AuthModel(
        email,
        password,
        confirmPassword,
        type
    )
    return authModel
}

fun createModel(cipher: Cipher, type: AuthorizeType): AuthModel {
    val model = AuthModel("", "", "", type)
    model.cipher = cipher
    return model
}