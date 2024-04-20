/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.serhii.apps.notes.R
import com.serhii.apps.notes.control.EventService
import com.serhii.apps.notes.control.NativeBridge
import com.serhii.core.utils.GoodUtils.Companion.getText

class ChangePasswordDialogUI : BaseDialogFragment() {

    @SuppressLint("InflateParams")
    override fun initView(inflater: LayoutInflater): ViewGroup {

        val dialogView = inflater.inflate(R.layout.change_password_dialog, null) as ViewGroup
        val title = dialogView.findViewById<TextView>(R.id.title_dialog)
        title.text = inflater.context.getString(R.string.change_password_title_dialog)

        val oldPassword = dialogView.findViewById<EditText>(R.id.edit_text_field)
        oldPassword.requestFocus()

        val newPassword = dialogView.findViewById<EditText>(R.id.new_password_field)

        val okButton = dialogView.findViewById<Button>(R.id.btn_ok)
        okButton.setOnClickListener { // Check entered password
            val success = NativeBridge.verifyPassword(getText(oldPassword))
            if (!success) {
                Toast.makeText(requireContext().applicationContext,
                    getString(R.string.change_password_toast_not_correct_password),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val result = EventService.onChangePassword(getText(newPassword))
                if (result) {
                    Toast.makeText(requireContext().applicationContext,
                        getString(R.string.change_password_toast_password_set), Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(requireContext().applicationContext,
                        getString(R.string.change_password_toast_password_error), Toast.LENGTH_LONG
                    ).show()
                }
                dismiss()
            }
        }
        // By default
        okButton.isEnabled = false

        val textChecker = TextChecker(oldPassword, newPassword, okButton)
        oldPassword.addTextChangedListener(textChecker)
        newPassword.addTextChangedListener(textChecker)

        val cancelButton = dialogView.findViewById<Button>(R.id.btn_cancel)
        cancelButton.setOnClickListener { dismiss() }

        return dialogView
    }

    private class TextChecker(
        private val oldPassword: EditText,
        private val newPassword: EditText,
        private val okButton: Button
    ) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            val enable = oldPassword.text.isNotEmpty() && newPassword.text.isNotEmpty()
            okButton.isEnabled = enable
        }
    }
}