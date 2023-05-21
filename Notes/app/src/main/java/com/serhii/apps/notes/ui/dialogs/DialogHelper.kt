/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.dialogs

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.serhii.apps.notes.R
import com.serhii.apps.notes.ui.ChangePasswordDialogUI
import com.serhii.apps.notes.ui.ClickListener
import com.serhii.apps.notes.ui.DialogWithEnterFiled
import com.serhii.apps.notes.ui.DialogWithEnterFiled.DialogListener
import com.serhii.apps.notes.ui.dialogs.base.AlertDialogHelper

object DialogHelper {

    private const val CPD_DIALOG_TAG = "Change password dialog"
    private const val SPD_DIALOG_TAG = "Set password dialog"
    private const val AL_DIALOG_TAG = "Alert dialog"

    @JvmStatic
    fun showChangePasswordDialog(activity: FragmentActivity) {
        val dialog = ChangePasswordDialogUI()
        dialog.show(activity.supportFragmentManager, CPD_DIALOG_TAG)
    }

    @JvmStatic
    fun showEnterPasswordField(
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
        activity: FragmentActivity,
        callback: ConfirmDialogCallback?,
        title: Int,
        message: Int
    ) {
        val titleString = activity.getString(title)
        val messageString = activity.getString(message)

        val alertDialog = com.serhii.apps.notes.ui.AlertDialog(object : ClickListener {
            override fun onConfirm() {
                callback?.onOkClicked()
            }

            override fun onCancel() {
                callback?.onCancelClicked()
            }
        }, titleString, messageString)

        alertDialog.show(activity.supportFragmentManager, AL_DIALOG_TAG)
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