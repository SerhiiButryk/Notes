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
import com.serhii.apps.notes.ui.EnterPasswordDialogUI
import com.serhii.apps.notes.ui.EnterPasswordDialogUI.DialogListener
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
    fun showEnterPasswordDialog(
        activity: FragmentActivity, listener: DialogListener?,
        title: String?, hint: String?) {

        val dialog = EnterPasswordDialogUI(listener)

        val args = Bundle()
        args.putString(EnterPasswordDialogUI.EXTRA_TITLE_TEXT, title)
        args.putString(EnterPasswordDialogUI.EXTRA_HINT_TEXT, hint)

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
                callback?.onOk()
            }

            override fun onCancel() {
                callback?.onCancel()
            }
        }, titleString, messageString)

        alertDialog.show(activity.supportFragmentManager, AL_DIALOG_TAG)
    }

    @JvmStatic
    fun showDialog(type: Int, context: Context) {
        val dialog = AlertDialogHelper(type, context)
        val builder = AlertDialog.Builder(context)
        builder.setPositiveButton(context.resources.getString(android.R.string.ok)) { _, _ -> }
        builder.setTitle(dialog.title)
        builder.setMessage(dialog.message)
        builder.show()
    }
}