package com.notes.os.impl

import androidx.compose.runtime.CompositionTracer
import androidx.compose.runtime.InternalComposeTracingApi
import androidx.tracing.DelicateTracingApi
import androidx.tracing.wire.TraceDriver
import androidx.tracing.wire.TraceSink
import java.io.File
import java.nio.file.Paths
import kotlin.concurrent.atomics.ExperimentalAtomicApi

class AppLogger : PlatformLog() {

    private val tracesFilePath: String
    private val driver: TraceDriver
    private val logFile: File

    init {
        val currDir = Paths.get("").toAbsolutePath()
        tracesFilePath = "$currDir/logs"
        logFile = File("$currDir/logs/logs.txt")
        driver = createTraceDriver()
        logi("Trace file: $tracesFilePath")
        logi("Log file: ${logFile.path}")
    }

    override fun logi(message: String) {
        val message = "NOTES [INFO] $message"
        println(message)
        logFile.appendText("$message\n")
    }

    override fun loge(message: String) {
        val message = "NOTES [ERROR] $message"
        println(message)
        logFile.appendText("$message\n")
    }

    override fun logd(message: String) {
        val message = "NOTES [DEBUG] $message"
        println(message)
        logFile.appendText("$message\n")
    }

    override fun close() {
        driver.close()
    }

    @OptIn(
        InternalComposeTracingApi::class,
        DelicateTracingApi::class,
        ExperimentalAtomicApi::class
    )
    override fun createCustomComposeTracer(): Any {
        return object : CompositionTracer {

            override fun isTraceInProgress(): Boolean {
                // Always enabled
                return isDebug.load()
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
                info: String
            ) {
                val closable = driver.tracer.beginSectionWithMetadata(category = "RecompositionTrace", name = info,
                    token = null, isRoot = false)
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
            val file = File(tracesFilePath)
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
            directory = outputDirectory
        )
    }
}