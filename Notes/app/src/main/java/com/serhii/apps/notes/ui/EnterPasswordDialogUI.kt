/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.serhii.apps.notes.R
import com.serhii.core.utils.GoodUtils
import com.serhii.core.utils.GoodUtils.Companion.getText

class EnterPasswordDialogUI(private val listener: DialogListener?) : BaseDialogFragment() {

    private lateinit var passwordField: EditText

    @SuppressLint("InflateParams")
    override fun initView(inflater: LayoutInflater): ViewGroup {

        val dialogView = inflater.inflate(R.layout.dialog_with_input_field, null) as ViewGroup
        val bundle = arguments

        val title = dialogView.findViewById<TextView>(R.id.title_dialog)
        title.text = bundle!!.getString(EXTRA_TITLE_TEXT)
        passwordField = dialogView.findViewById<EditText>(R.id.edit_text_field)
        passwordField.requestFocus()

        val info = dialogView.findViewById<TextView>(R.id.info_dialog)
        info.text = bundle.getString(EXTRA_HINT_TEXT)

        val okButton = dialogView.findViewById<Button>(R.id.btn_ok)
        okButton.setOnClickListener {
            if (listener != null) {
                listener.onOk(getText(passwordField), context)
                dismiss()
            }
        }

        // By default
        okButton.setEnabled(false)
        val textChecker = TextChecker(passwordField, okButton)
        passwordField.addTextChangedListener(textChecker)
        val cancelButton = dialogView.findViewById<Button>(R.id.btn_cancel)
        cancelButton.setOnClickListener {
            listener?.onCancel(context)
            dismiss()
        }

        return dialogView
    }

    override fun onResume() {
        super.onResume()
        GoodUtils.showKeyboard(requireContext(), passwordField)
    }

    private class TextChecker(private val password: EditText, private val okButton: Button) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            val enable = password.text.isNotEmpty()
            okButton.isEnabled = enable
        }
    }

    interface DialogListener {
        fun onOk(enteredText: String?, context: Context?)
        fun onCancel(context: Context?)
    }

    companion object {
        const val EXTRA_TITLE_TEXT = "dialog title"
        const val EXTRA_HINT_TEXT = "dialog hint"
    }
}