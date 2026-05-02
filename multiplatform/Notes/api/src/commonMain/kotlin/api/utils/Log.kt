package api.utils

interface Log {
    fun logi(message: String)
    fun loge(message: String)
    fun logd(message: String)
    fun setDebug(isDebug: Boolean)
}