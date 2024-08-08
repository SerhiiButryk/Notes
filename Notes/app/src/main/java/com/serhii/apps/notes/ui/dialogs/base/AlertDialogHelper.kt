/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.dialogs.base

import android.content.Context
import com.serhii.apps.notes.R
import com.serhii.apps.notes.control.NativeBridge
import com.serhii.apps.notes.control.auth.types.AuthResult
import com.serhii.core.utils.GoodUtils.Companion.formatString

class AlertDialogHelper(private val type: Int, context: Context) {

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
            val limitLeft = NativeBridge.unlockLimit
            title = context.getString(R.string.title_wrong_password)
            message = formatString(context.getString(R.string.ms_wrong_password), limitLeft)
        } else if (type == AuthResult.EMPTY_FIELD.typeId) {
            title = context.getString(R.string.title_empty_field)
            message = context.getString(R.string.ms_empty_field)
        } else if (type == AuthResult.USER_NAME_EXISTS.typeId) {
            title = context.getString(R.string.title_info)
            message = context.getString(R.string.ms_user_exists)
        } else if (type == AuthResult.PASSWORD_DIFFERS.typeId) {
            title = context.getString(R.string.title_info)
            message = context.getString(R.string.ms_password_differs)
        } else if (type == AuthResult.SPACE_CONTAIN.typeId) {
            title = context.getString(R.string.title_info)
            message = context.getString(R.string.ms_space_contain)
        } else if (type == AuthResult.UNLOCK_KEY_INVALID.typeId) {
            title = context.getString(R.string.title_unlock_key_invalid)
            message = context.getString(R.string.ms_unlock_key_invalid)
        } else if (type == ALERT_DIALOG_TYPE_BACKUP_ERROR) {
            title = context.getString(R.string.title_extract_data)
            message = context.getString(R.string.ms_extract_data)
        } else if (type == AuthResult.ALERT_DIALOG_TYPE_PASSWORD_IS_SHORT.typeId) {
            title = context.getString(R.string.title_error)
            message = context.getString(R.string.ms_password_is_short)
        } else if (type == AuthResult.EMAIL_INVALID.typeId) {
            title = context.getString(R.string.title_error)
            message = context.getString(R.string.ms_email_is_not_valid)
        }
    }

    companion object {
        // Errors
        const val ALERT_DIALOG_TYPE_BACKUP_ERROR = 101

        fun getTitleFor(type: Int): Int {

            var title: Int = -1

            if (type == AuthResult.ACCOUNT_INVALID.typeId)
                title = R.string.title_no_user_account
            else if (type == AuthResult.WRONG_PASSWORD.typeId)
                title = R.string.title_wrong_password
            else if (type == AuthResult.EMPTY_FIELD.typeId)
                title = R.string.title_empty_field
            else if (type == AuthResult.USER_NAME_EXISTS.typeId)
                title = R.string.title_info
            else if (type == AuthResult.PASSWORD_DIFFERS.typeId)
                title = R.string.title_info
            else if (type == AuthResult.SPACE_CONTAIN.typeId)
                title = R.string.title_info
            else if (type == AuthResult.UNLOCK_KEY_INVALID.typeId)
                title = R.string.title_unlock_key_invalid
            else if (type == ALERT_DIALOG_TYPE_BACKUP_ERROR)
                title = R.string.title_extract_data
            else if (type == AuthResult.ALERT_DIALOG_TYPE_PASSWORD_IS_SHORT.typeId)
                title = R.string.title_error
            else if (type == AuthResult.EMAIL_INVALID.typeId)
                title = R.string.title_error

            return title
        }

        fun getMessageFor(type: Int): Int {

            var message: Int = -1

            if (type == AuthResult.ACCOUNT_INVALID.typeId) {
                message = R.string.ms_no_user_account
            } else if (type == AuthResult.WRONG_PASSWORD.typeId) {
                message = R.string.ms_wrong_password
            } else if (type == AuthResult.EMPTY_FIELD.typeId) {
                message = R.string.ms_empty_field
            } else if (type == AuthResult.USER_NAME_EXISTS.typeId) {
                message = R.string.ms_user_exists
            } else if (type == AuthResult.PASSWORD_DIFFERS.typeId) {
                message = R.string.ms_password_differs
            } else if (type == AuthResult.SPACE_CONTAIN.typeId) {
                message = R.string.ms_space_contain
            } else if (type == AuthResult.UNLOCK_KEY_INVALID.typeId) {
                message = R.string.ms_unlock_key_invalid
            } else if (type == ALERT_DIALOG_TYPE_BACKUP_ERROR) {
                message = R.string.ms_extract_data
            } else if (type == AuthResult.ALERT_DIALOG_TYPE_PASSWORD_IS_SHORT.typeId) {
                message = R.string.ms_password_is_short
            } else if (type == AuthResult.EMAIL_INVALID.typeId) {
                message = R.string.ms_email_is_not_valid
            }

            return message
        }
    }
}