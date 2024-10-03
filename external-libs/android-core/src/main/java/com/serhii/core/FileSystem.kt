/*
 * Copyright 2024. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.core

import android.content.Context

object FileSystem {

    fun setFilePath(context: Context) {
        val filePath = getFilePath(context)
        _setSystemFilePath(filePath)
    }

    private fun getFilePath(context: Context): String {
        return context.filesDir.absolutePath
    }

    private external fun _setSystemFilePath(path: String)
}