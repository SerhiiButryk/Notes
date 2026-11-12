package com.notes.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composer
import androidx.compose.runtime.CompositionTracer
import androidx.compose.runtime.InternalComposeTracingApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.configureSwingGlobalsForCompose
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.awaitApplication
import api.Platform
import com.notes.os.JVMInitProvider
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

const val APP_NAME = "Notes"

fun run(block: () -> Unit) {

    val home = System.getProperty("user.home")
    val logFile = java.io.File(home, "app_crash.log")

    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
        logFile.appendBytes(throwable.stackTraceToString().toByteArray())
        throwable.printStackTrace()
    }

    try {
        // Perform some initialization at this point
        initApplication()
        block()
    } catch (e: Exception) {
        logFile.appendBytes(e.stackTraceToString().toByteArray())
        e.printStackTrace()
    }
}

fun initApplication() {
    val osType = System.getProperty("os.name").lowercase()
    if (osType.contains("mac")) {
        // Set application title
        System.setProperty("apple.awt.application.name", APP_NAME)
    }
    JVMInitProvider.onCreate()
}

@OptIn(ExperimentalComposeUiApi::class, InternalComposeTracingApi::class)
fun applicationTraced(
    exitProcessOnExit: Boolean = true,
    content: @Composable ApplicationScope.() -> Unit,
) {
    if (System.getProperty("compose.application.configure.swing.globals") == "true") {
        configureSwingGlobalsForCompose()
    }

    // Set up a custom compose tracer before calling Compose functions
    val tracer = Platform().logger.createCustomComposeTracer()
    Composer.setTracer(tracer as CompositionTracer)

    runBlocking {
        awaitApplication {
            content()
        }
    }

    if (exitProcessOnExit) {
        JVMInitProvider.onDestroy()
        println("Process has finished")
        exitProcess(0)
    }
}
