package api.net

/**
 * Http client abstraction
 */
interface HttpClient {
    suspend fun post(
        url: String,
        formArgs: Map<String, String>,
    ): String?

    fun postSync(
        url: String,
        formArgs: Map<String, String>,
    ): String?
}
