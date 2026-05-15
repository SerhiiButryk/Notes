package com.notes.app.notes

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

const val APP_TITLE = "Notes"

fun run(
    block: () -> Unit
) {

    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
        println("Uncaught exception in thread: ${thread.name}, throwable: $throwable")
        throwable.printStackTrace()
    }

    try {
        // Perform some initialization at this point
        initApplication()
        block()
    } catch (e: Exception) {
        println("Uncaught exception: $e")
    }
}

fun initApplication() {
    val osType = System.getProperty("os.name").lowercase()
    if (osType.contains("mac")) {
        // Set application title
        System.setProperty("apple.awt.application.name", APP_TITLE)
    }
    JVMInitProvider.create()
}

@OptIn(ExperimentalComposeUiApi::class, InternalComposeTracingApi::class)
fun applicationTraced(
    exitProcessOnExit: Boolean = true,
    content: @Composable ApplicationScope.() -> Unit
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
        Platform().logger.close()
        exitProcess(0)
    }
}