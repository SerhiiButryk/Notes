/*
 * Copyright 2024. Happy coding ! :)
 * Author: Serhii Butryk
 */

package com.serhii.core

import android.content.Context
import com.serhii.core.utils.GoodUtils

object FileSystem {

    fun setFilePath(context: Context) {
        val filePath = GoodUtils.getFilePath(context)
        _setSystemFilePath(filePath)
    }

    private external fun _setSystemFilePath(path: String)
}