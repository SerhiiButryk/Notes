package com.notes.os.impl

import androidx.compose.runtime.CompositionTracer
import androidx.compose.runtime.InternalComposeTracingApi
import androidx.tracing.DelicateTracingApi
import androidx.tracing.wire.TraceDriver
import androidx.tracing.wire.TraceSink
import api.Platform
import api.data.AppSettings
import api.utils.Log
import java.io.File

class AppLogger : Log() {

    private val logsDir: String = "${Platform().getCacheDir()}/logs"
    private val logFile: File = File("$logsDir/${getLogFileName()}")

    private val driver: TraceDriver

    init {
        driver = createTraceDriver()
    }

    override fun getPID(): Long {
        return ProcessHandle.current().pid()
    }

    override fun logi(message: String): String {
        println(super.logi(message))
        return ""
    }

    override fun loge(message: String): String {
        println(super.loge(message))
        return ""
    }

    override fun logd(message: String): String {
        if (AppSettings.isDebugEnabled) {
            println(super.logd(message))
        }
        return ""
    }

    override fun logToFile(message: String) {
        logFile.appendText("$message\n")
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
                // TODO Uncomment if debugging UI
                return false
            }

            override fun traceEventEnd() {
                // This is a fast then just saving a closable and then close it here
                val process = driver.context.process
                val thread = Thread.currentThread()
                val threadTrack = process.getOrCreateThreadTrack(
                    thread.threadId(),
                    ProcessHandle.current().pid(),
                    thread.name,
                )
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
