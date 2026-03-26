package com.notes.net

import api.Platform
import api.net.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import java.net.ConnectException

class NetHttpClient : HttpClient {
    private val client = OkHttpClient()

    private val tag = "HttpClient"

    override suspend fun post(
        url: String,
        body: String,
        mimeType: String,
        callback: (statusCode: Int, body: InputStream?) -> Unit,
    ) {
        withContext(Dispatchers.IO) {
            Platform().logger.logi("$tag: post: started")

            try {
                val request =
                    Request
                        .Builder()
                        .url(url)
                        .post(body.toRequestBody(mimeType.toMediaType()))
                        .build()

                client.newCall(request).execute().use { response ->
                    val result = if (response.isSuccessful) "successful" else "failed"
                    Platform().logger.logi("$tag: post: $result, status code = ${response.code}")
                    callback(response.code, response.body?.byteStream())
                }
            } catch (e: ConnectException) {
                // TODO: Improve error handling
                Platform().logger.loge("$tag: error: $e")
                callback(-1, null)
            }
        }
    }
}
