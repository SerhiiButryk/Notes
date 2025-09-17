package com.notes.api

import java.io.InputStream

/**
 * Http client abstraction
 */
interface HttpClient {
    suspend fun post(url: String, body: String, mimeType: String, callback: (statusCode: Int, body: InputStream?) -> Unit)
}