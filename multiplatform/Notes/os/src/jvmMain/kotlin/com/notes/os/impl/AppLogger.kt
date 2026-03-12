package com.notes.os.impl

import androidx.compose.runtime.CompositionTracer
import androidx.compose.runtime.InternalComposeTracingApi
import androidx.tracing.DelicateTracingApi
import androidx.tracing.wire.TraceDriver
import androidx.tracing.wire.TraceSink
import api.data.AppSettings
import java.io.File
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AppLogger : PlatformLog() {

    private val logsDir: String
    private val driver: TraceDriver
    private val logFile: File

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    init {
        val currDir = Paths.get("").toAbsolutePath()
        logsDir = "$currDir/logs"
        logFile = File("$logsDir/logs.txt")
        driver = createTraceDriver()
        logi("App launched >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
        logi("Logs: ${logFile.parent}")
    }

    override fun logi(message: String) {
        val log = format(message, "INFO")
        println(log)
        logFile.appendText("$log\n")
    }

    override fun loge(message: String) {
        val log = format(message, "ERROR")
        println(log)
        logFile.appendText("$log\n")
    }

    override fun logd(message: String) {
        val log = format(message, "DEBUG")
        println(log)
        logFile.appendText("$log\n")
    }

    private fun format(message: String, level: String): String {
        val timestamp = LocalDateTime.now().format(formatter)
        val tid = Thread.currentThread().threadId()
        val pid = ProcessHandle.current().pid()
        return "[$pid:$tid | $timestamp] NOTES [$level] $message"
    }

    override fun close() {
        driver.close()
    }

    @OptIn(
        InternalComposeTracingApi::class,
        DelicateTracingApi::class,
    )
    override fun createCustomComposeTracer(): Any {
        return object : CompositionTracer {
            override fun isTraceInProgress(): Boolean {
                return AppSettings.isDebugEnabled
            }

            override fun traceEventEnd() {
                // This is a fast then just saving a closable and then close it here
                val process = driver.context.process
                val thread = Thread.currentThread()
                val threadTrack = process.getOrCreateThreadTrack(thread.id, thread.name)
                threadTrack.endSection()
            }

            override fun traceEventStart(
                key: Int,
                dirty1: Int,
                dirty2: Int,
                info: String,
            ) {
                val closable =
                    driver.tracer.beginSectionWithMetadata(
                        category = "RecompositionTrace",
                        name = info,
                        token = null,
                        isRoot = false,
                    )
                // We can decide whether we wanna dispatch a trace here
                closable.metadata.dispatchToTraceSink()
            }
        }
    }

    private fun createTraceDriver(): TraceDriver {
        val driver = TraceDriver(sink = createSink())
        return driver
    }

    private fun createSink(): TraceSink {
        val outputDirectory =
            try {
                val file = File(logsDir)
                if (!file.exists()) {
                    file.mkdirs()
                }
                file
            } catch (e: Exception) {
                println("Exception: $e")
                e.printStackTrace()
                throw e
            }
        return TraceSink(
            sequenceId = 1,
            directory = outputDirectory,
        )
    }
}
