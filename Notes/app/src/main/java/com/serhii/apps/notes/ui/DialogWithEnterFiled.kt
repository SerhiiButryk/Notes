/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.serhii.apps.notes.R
import com.serhii.core.utils.GoodUtils.Companion.getText

class DialogWithEnterFiled(private val listener: DialogListener?) : BaseDialogFragment() {

    @SuppressLint("InflateParams")
    override fun initView(inflater: LayoutInflater): ViewGroup {

        val dialogView = inflater.inflate(R.layout.dialog_with_input_field, null) as ViewGroup
        val bundle = arguments

        val title = dialogView.findViewById<TextView>(R.id.title_dialog)
        title.text = bundle!!.getString(EXTRA_TITLE_TEXT)
        val editTextField = dialogView.findViewById<EditText>(R.id.edit_text_field)
        editTextField.hint = bundle.getString(EXTRA_HINT_TEXT)
        editTextField.requestFocus()

        val okButton = dialogView.findViewById<Button>(R.id.btn_ok)
        okButton.setOnClickListener {
            if (listener != null) {
                listener.onOkClicked(getText(editTextField))
                dismiss()
            }
        }

        // By default
        okButton.setEnabled(false)
        val textChecker = TextChecker(editTextField, okButton)
        editTextField.addTextChangedListener(textChecker)
        val cancelButton = dialogView.findViewById<Button>(R.id.btn_cancel)
        cancelButton.setOnClickListener {
            listener?.onCancelClicked()
            dismiss()
        }

        return dialogView
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
        fun onOkClicked(enteredText: String?)
        fun onCancelClicked()
    }

    companion object {
        const val EXTRA_TITLE_TEXT = "dialog title"
        const val EXTRA_HINT_TEXT = "dialog hint"
    }
}