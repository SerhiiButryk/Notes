package api.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

abstract class Log {

    protected val tag = "Notes"

    abstract fun getPID(): Long

    open suspend fun hasLogFile(): Boolean = true

    open fun init(ctx: Any? = null) {}

    open fun logi(message: String): String {
        val mes = format(message, "INFO")
        logToFile(mes)
        return mes
    }

    open fun loge(message: String): String  {
        val mes = format(message, "ERROR")
        logToFile(mes)
        return mes
    }

    open fun logd(message: String): String  {
        val mes = format(message, "DEBUG")
        logToFile(mes)
        return mes
    }

    open fun close() {}

    open fun createCustomComposeTracer(): Any = throw RuntimeException("Not supported")

    open fun logMemoryUsage(params: Any? = null) {}

    fun getLogFileName(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val timestamp = LocalDateTime.now().format(formatter)
        return "${timestamp}.logs"
    }

    open fun logToFile(message: String) {}

    fun format(message: String, level: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        val timestamp = LocalDateTime.now().format(formatter)
        val tid = Thread.currentThread().id
        val pid = getPID()
        val tname = Thread.currentThread().name
        return "[$pid:$tid:$tname | $timestamp] $tag [$level] $message"
    }

}
