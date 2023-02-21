/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.dialogs.base

import android.content.Context
import com.serhii.apps.notes.R
import com.serhii.apps.notes.control.NativeBridge
import com.serhii.apps.notes.control.auth.types.AuthResult
import com.serhii.apps.notes.ui.dialogs.DialogHelper
import com.serhii.core.utils.GoodUtils.Companion.formatString

class AlertDialogHelper(private val type: Int, context: Context) {

    // User entered short password
    private val ALERT_DIALOG_TYPE_PASSWORD_IS_SHORT = -12

    var title = ""
        private set

    var message = ""
        private set

    init {
        setStrings(context)
    }

    private fun setStrings(context: Context) {
        if (type == AuthResult.ACCOUNT_INVALID.typeId) {
            title = context.getString(R.string.title_no_user_account)
            message = context.getString(R.string.ms_no_user_account)
        } else if (type == AuthResult.WRONG_PASSWORD.typeId) {
            val nativeBridge = NativeBridge()
            val limitLeft = nativeBridge.limitLeft
            title = context.getString(R.string.title_wrong_password)
            message = formatString(context.getString(R.string.ms_wrong_password), limitLeft)
        } else if (type == AuthResult.EMPTY_FIELD.typeId) {
            title = context.getString(R.string.title_empty_field)
            message = context.getString(R.string.ms_empty_field)
        } else if (type == AuthResult.USER_NAME_EXISTS.typeId) {
            title = context.getString(R.string.title_user_exists)
            message = context.getString(R.string.ms_user_exists)
        } else if (type == AuthResult.PASSWORD_DIFFERS.typeId) {
            title = context.getString(R.string.title_password_differs)
            message = context.getString(R.string.ms_password_differs)
        } else if (type == AuthResult.SPACE_CONTAIN.typeId) {
            title = context.getString(R.string.title_space_contain)
            message = context.getString(R.string.ms_space_contain)
        } else if (type == AuthResult.UNLOCK_KEY_INVALID.typeId) {
            title = context.getString(R.string.title_unlock_key_invalid)
            message = context.getString(R.string.ms_unlock_key_invalid)
        } else if (type == DialogHelper.ALERT_DIALOG_TYPE_BACKUP_ERROR) {
            title = context.getString(R.string.title_extract_data)
            message = context.getString(R.string.ms_extract_data)
        } else if (type == ALERT_DIALOG_TYPE_PASSWORD_IS_SHORT) {
            title = context.getString(R.string.title_error)
            message = context.getString(R.string.ms_password_is_short)
        }
    }
}