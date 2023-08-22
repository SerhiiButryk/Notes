/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.utils

import android.widget.EditText
import android.widget.Toast
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Message
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.text.set
import com.serhii.core.BuildConfig
import java.text.SimpleDateFormat
import java.util.*

/**
 * Library helper functions
 */
class GoodUtils {

    companion object {

        private const val DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"

        private val keyboardHelper = KeyboardHelper()

        @JvmStatic
        fun buildMessage(what: Int): Message {
            val ms = Message.obtain()
            ms.what = what
            return ms
        }

        @JvmStatic
        fun buildMessage(what: Int, obj: Any?): Message {
            val ms = Message.obtain()
            ms.what = what
            ms.obj = obj
            return ms
        }

        /**
         * Retrieve an absolute file path for storing files in the internal app storage
         */
        @JvmStatic
        fun getFilePath(context: Context): String {
            return context.filesDir.absolutePath
        }

        /**
         * Retrieve a text from a EditText widget
         */
        @JvmStatic
        fun getText(editText: EditText): String {
            return editText.text.toString().trim { it <= ' ' }
        }

        /**
         * Fills string with passed variables
         */
        @JvmStatic
        fun <T> formatString(message: String, vararg placeValues: T): String {
            return String.format(message, *placeValues)
        }

        @JvmStatic
        fun formatString(message: String, placeValue: String): String {
            return message.replace("%", placeValue)
        }

        /**
         * Returns current timestamp in the format yyyy-MM-dd HH:mm:ss
         */
        @JvmStatic
        fun currentTimeToString(): String {
            val dateFormat = SimpleDateFormat(DEFAULT_TIME_FORMAT, Locale.getDefault())
            return dateFormat.format(Date())
        }

        /**
         * Show toast helper function
         */
        @JvmStatic
        fun showToast(context: Context, stringId: Int) {
            Toast.makeText(context, context.getString(stringId), Toast.LENGTH_SHORT).show()
        }

        /**
         * Enable unsecured screen content settings if it's release build
         */
        @JvmStatic
        fun enableUnsecureScreenProtection(activity: Activity): Boolean {
            // Enable for release build
            if (!BuildConfig.DEBUG) {
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
                return true
            }
            return false
        }

        @JvmStatic
        fun showKeyboard(context: Context, view: View) {
            keyboardHelper.requestHowKeyboard(context, view)
        }

        @JvmStatic
        fun hideKeyboard(context: Context, view: View) {
            keyboardHelper.requestHideKeyboard(context, view)
        }

        @JvmStatic
        fun setTextHighlighting(ranges: List<IntRange>, view: TextView, content: String) {
            val selection = SpannableString(content)
            for (range in ranges) {
                selection[range] = BackgroundColorSpan(Color.YELLOW)
                selection[range] = ForegroundColorSpan(Color.BLACK)
            }
            view.text = selection
        }
    }
}