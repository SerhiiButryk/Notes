/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.data_model

import com.serhii.apps.notes.control.auth.types.AuthorizeType

data class AuthModel(var email: String = "", var password: String = "",
                     var confirmPassword: String = "", var authType: AuthorizeType?)