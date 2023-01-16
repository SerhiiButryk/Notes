/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.apps.notes.ui.utils

import android.widget.EditText
import android.text.TextWatcher
import android.text.Editable
import android.text.TextUtils
import android.widget.Button

/**
 * Helper class for enabling button if text has changed
 */
class TextChecker(private val controlField: EditText, private val controlButton: Button) : TextWatcher {

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable) {
        val enable = !TextUtils.isEmpty(controlField.text)
        controlButton.isEnabled = enable
    }

}