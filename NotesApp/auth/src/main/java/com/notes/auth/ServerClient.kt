package com.notes.auth

import com.notes.interfaces.PlatformAPIs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

class ServerClient {

    val client = OkHttpClient()
    val coroutineScope = CoroutineScope(Dispatchers.Default)

    fun request(email: String, pass: String, confirmPass: String) {

        coroutineScope.launch {

            val request = Request.Builder()
                // 10.0.2.2 refers to localhost
                .url("http://10.0.2.2:8085/auth/register")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    PlatformAPIs.log.log("request: failed code = ${response.code}")
                } else {
                    PlatformAPIs.log.log("request: successful code = ${response.code}")
                }
                PlatformAPIs.log.log("request: body = ${response.body}")
            }

        }

    }

}