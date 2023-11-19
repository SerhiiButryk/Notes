/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.data_model

import com.serhii.apps.notes.control.auth.types.AuthorizeType

data class AuthCredsModel(val email: String = "", val password: String = "",
                          val confirmPassword: String = "", val authType: AuthorizeType = AuthorizeType.UN_SET)