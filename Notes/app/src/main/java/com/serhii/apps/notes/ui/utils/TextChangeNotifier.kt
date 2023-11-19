/*
 * Copyright 2023. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes.ui.utils

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import com.serhii.apps.notes.control.idle_lock.IdleLockHandler

class TextChangeNotifier(val context: Context) : TextWatcher {

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        // not used
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        // not used
    }

    override fun afterTextChanged(p0: Editable?) {
        // Issue description:
        // App gets locked if user interacts with it by typing some text in Edit field
        // We should reset inactivity timer if user has entered a text in Edit fields
        IdleLockHandler.onUserInteraction(context)
    }
}