/*
 * Copyright 2023. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.core.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager

internal class KeyboardHelper {

    private var imm : InputMethodManager? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    private class CommandShowKeyboard(private val view: View, private val imm: InputMethodManager) : Runnable {

        private val mainHandler = Handler(Looper.getMainLooper())
        private val maxRetryCount = 4
        private var counter: Int = 0

        override fun run() {
            val success = imm.showSoftInput(view, 0)
            if (!success && counter < maxRetryCount) {
                mainHandler.postDelayed(this, 100)
                counter++
            }
        }
    }

    fun requestHowKeyboard(context: Context, view: View) {

        if (imm == null) {
            imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        }

        mainHandler.post(CommandShowKeyboard(view, imm!!))
    }

    fun requestHideKeyboard(context: Context, view: View) {

        if (imm == null) {
            imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        }

        mainHandler.post {
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}