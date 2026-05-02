package com.notes.net

import api.Platform
import api.net.HttpClient
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

class NetHttpClient : HttpClient {

    private val tag = "HttpClient"

    private val client = OkHttpClient()

    override suspend fun post(
        url: String,
        formArgs: Map<String, String>,
    ): String? {
        return suspendCancellableCoroutine { continuation ->
            postInternal(url = url, formArgs = formArgs, callback = { body, result ->
                if (result) {
                    continuation.resume(body) { _, _, _ ->
                        // no-op if coroutine is canceled
                    }
                } else {
                    continuation.resume(null) { _, _, _ ->
                        // no-op if coroutine is canceled
                    }
                }
            })
        }
    }

    override fun postSync(
        url: String,
        formArgs: Map<String, String>
    ): String? = postInternal(url = url, formArgs = formArgs, sync = true)

    private fun postInternal(
        url: String,
        formArgs: Map<String, String>,
        callback: ((String?, Boolean) -> Unit)? = null,
        sync: Boolean = false
    ): String? {
        try {

            val builder = FormBody.Builder()
            for ((key, value) in formArgs) {
                builder.add(key, value)
            }
            val body = builder.build()

            val request = Request.Builder().url(url).post(body).build()

            if (sync) {
                Platform().logger.logi("$tag: postInternal: sync sending...")
                val response = client.newCall(request).execute()
                return response.body.string()
            } else {
                Platform().logger.logi("$tag: postInternal: async sending...")
                client.newCall(request).execute().use { response ->
                    val result = if (response.isSuccessful) "successful" else "failed"
                    Platform().logger.logi("$tag: postInternal: '$result', status code = ${response.code}")
                    callback?.invoke(response.body.string(), true)
                }
            }

        } catch (e: Exception) {
            // TODO: Improve error handling
            Platform().logger.loge("$tag: postInternal: error: '$e'")
            callback?.invoke(null, false)
        }

        Platform().logger.logi("$tag: postInternal: done")
        return null
    }
}
