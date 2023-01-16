/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.dialogs

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.serhii.apps.notes.R
import com.serhii.apps.notes.ui.ChangePasswordDialogUI
import com.serhii.apps.notes.ui.DialogWithEnterFiled
import com.serhii.apps.notes.ui.DialogWithEnterFiled.DialogListener
import com.serhii.apps.notes.ui.dialogs.base.AlertDialogHelper

object DialogHelper {

    private const val CPD_DIALOG_TAG = "Change password dialog"
    private const val SPD_DIALOG_TAG = "Set password dialog"

    const val ALERT_DIALOG_TYPE_BACKUP_ERROR = 101
    const val ALERT_DIALOG_TYPE_PASSWORD_IS_WEAK = 102

    @JvmStatic
    fun showChangePasswordDialog(activity: FragmentActivity) {
        val dialog = ChangePasswordDialogUI()
        dialog.show(activity.supportFragmentManager, CPD_DIALOG_TAG)
    }

    @JvmStatic
    fun showDialogWithEnterField(
        activity: FragmentActivity, listener: DialogListener?,
        title: String?, hint: String?) {

        val dialog = DialogWithEnterFiled(listener)

        val args = Bundle()
        args.putString(DialogWithEnterFiled.EXTRA_TITLE_TEXT, title)
        args.putString(DialogWithEnterFiled.EXTRA_HINT_TEXT, hint)

        dialog.arguments = args
        dialog.show(activity.supportFragmentManager, SPD_DIALOG_TAG)
    }

    @JvmStatic
    fun showConfirmDialog(
        activity: Activity,
        callback: ConfirmDialogCallback?,
        title: Int,
        message: Int
    ) {
        val builder = AlertDialog.Builder(activity)
        builder.setPositiveButton(activity.resources.getString(R.string.kbtn_ok)) { _, _ -> callback?.onOkClicked() }
        builder.setNegativeButton(activity.resources.getString(R.string.kbtn_cancel)) { _, _ -> callback?.onCancelClicked() }
        builder.setTitle(title)
        builder.setMessage(message)
        builder.create().show()
    }

    @JvmStatic
    fun showAlertDialog(type: Int, context: Context) {
        val dialog = AlertDialogHelper(type, context)
        val builder = AlertDialog.Builder(context)
        builder.setPositiveButton(context.resources.getString(R.string.kbtn_ok)) { _, _ -> }
        builder.setTitle(dialog.title)
        builder.setMessage(dialog.message)
        builder.show()
    }
}