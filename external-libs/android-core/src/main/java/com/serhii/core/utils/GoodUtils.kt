/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */
package com.serhii.core.utils

import android.widget.EditText
import com.serhii.core.utils.GoodUtils
import android.widget.Toast
import android.app.Activity
import android.content.Context
import android.os.Message
import android.view.WindowManager
import com.serhii.core.BuildConfig
import java.text.SimpleDateFormat
import java.util.*

/**
 * Library helper functions
 */
class GoodUtils {

    companion object {

        private const val DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"

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
    }
}