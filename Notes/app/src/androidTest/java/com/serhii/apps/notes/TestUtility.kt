/*
 * Copyright 2022. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.apps.notes

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.FileReader
import java.io.InputStream

object TestUtility {

    /**
     * Read file in assets and returns empty string in case of errors
     */
    fun readFileFromTestAssets(filename: String) : String {
        val context = InstrumentationRegistry.getInstrumentation().context
        var text = ""
        var inputStream : InputStream? = null
        try {
            inputStream = context.assets.open(filename)
            inputStream = BufferedInputStream(inputStream)
            Log.i(
                TAG, "readFileFromTestAssets(), going to read ${inputStream.available()}" +
                        "bytes in file $filename")

            val byteArray = ByteArray(1024)
            while (inputStream.read(byteArray) != -1) {
                text += String(byteArray)
            }

        } catch (e: Exception) {
            Log.i(TAG, "readFileFromTestAssets(), error: $e")
            e.printStackTrace()
        } finally {
            inputStream?.close()
        }
        return text
    }

    private val TAG = TestUtility::class.java.simpleName

}