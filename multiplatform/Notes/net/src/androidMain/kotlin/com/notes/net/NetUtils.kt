package com.notes.net

import api.Platform
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

private val tag = "NetUtils"

fun inputStreamAsString(inputStream: InputStream?): String {
    if (inputStream == null) {
        Platform().logger.loge("$tag inputStreamAsString: input stream is null")
        return ""
    }

    val bufferedReader = BufferedReader(InputStreamReader(inputStream))
    val stringBuilder = StringBuilder()

    try {
        while (true) {
            val line = bufferedReader.readLine() ?: break
            stringBuilder.append(line)
        }
    } catch (e: Exception) {
        Platform().logger.loge("$tag inputStreamAsString: error: $e")
    } finally {
        inputStream.close()
    }

    return stringBuilder.toString()
}
