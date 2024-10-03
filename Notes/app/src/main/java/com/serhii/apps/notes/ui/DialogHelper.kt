/*
 * Copyright 2024. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui

import com.serhii.apps.notes.R
import com.serhii.apps.notes.control.auth.types.AppErrors

object DialogHelper {

    // Errors
    private const val ALERT_DIALOG_TYPE_BACKUP_ERROR = 101

    fun getTitleFor(type: Int): Int {

        var title: Int = -1

        if (type == AppErrors.ACCOUNT_INVALID.typeId)
            title = R.string.title_error
        else if (type == AppErrors.WRONG_PASSWORD.typeId)
            title = R.string.title_error
        else if (type == AppErrors.EMPTY_FIELD.typeId)
            title = R.string.title_error
        else if (type == AppErrors.USER_NAME_EXISTS.typeId)
            title = R.string.title_info
        else if (type == AppErrors.PASSWORD_DIFFERS.typeId)
            title = R.string.title_info
        else if (type == AppErrors.SPACE_CONTAIN.typeId)
            title = R.string.title_info
        else if (type == AppErrors.UNLOCK_KEY_INVALID.typeId)
            title = R.string.title_error
        else if (type == ALERT_DIALOG_TYPE_BACKUP_ERROR)
            title = R.string.title_error
        else if (type == AppErrors.ALERT_DIALOG_TYPE_PASSWORD_IS_SHORT.typeId)
            title = R.string.title_error
        else if (type == AppErrors.EMAIL_INVALID.typeId)
            title = R.string.title_error

        return title
    }

    fun getMessageFor(type: Int): Int {

        var message: Int = -1

        if (type == AppErrors.ACCOUNT_INVALID.typeId) {
            message = R.string.ms_no_user_account
        } else if (type == AppErrors.WRONG_PASSWORD.typeId) {
            message = R.string.ms_wrong_password
        } else if (type == AppErrors.EMPTY_FIELD.typeId) {
            message = R.string.ms_empty_field
        } else if (type == AppErrors.USER_NAME_EXISTS.typeId) {
            // We should never fall in this case
            throw RuntimeException("USER_NAME_EXISTS error")
        } else if (type == AppErrors.PASSWORD_DIFFERS.typeId) {
            message = R.string.ms_password_differs
        } else if (type == AppErrors.SPACE_CONTAIN.typeId) {
            message = R.string.ms_space_contain
        } else if (type == AppErrors.UNLOCK_KEY_INVALID.typeId) {
            message = R.string.title_error
        } else if (type == ALERT_DIALOG_TYPE_BACKUP_ERROR) {
            message = R.string.ms_extract_data
        } else if (type == AppErrors.ALERT_DIALOG_TYPE_PASSWORD_IS_SHORT.typeId) {
            message = R.string.ms_password_is_short
        } else if (type == AppErrors.EMAIL_INVALID.typeId) {
            message = R.string.ms_email_is_not_valid
        }

        return message
    }

}