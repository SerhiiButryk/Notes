/*
 * Copyright 2024. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui

import com.serhii.apps.notes.R
import com.serhii.apps.notes.control.auth.types.AuthResult

object DialogHelper {

    // Errors
    private const val ALERT_DIALOG_TYPE_BACKUP_ERROR = 101

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