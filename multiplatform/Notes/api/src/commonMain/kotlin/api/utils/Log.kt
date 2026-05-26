package api.utils

import java.io.Closeable

interface Log : Closeable {
    fun logi(message: String)
    fun loge(message: String)
    fun logd(message: String)
    fun setDebug(isDebug: Boolean)
    fun createCustomComposeTracer(): Any
    fun logMemoryUsage(params: Any? = null) {}
}