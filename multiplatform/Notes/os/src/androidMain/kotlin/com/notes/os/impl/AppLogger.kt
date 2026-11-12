package com.notes.os.impl

import android.app.ActivityManager
import android.content.Context
import android.net.Uri
import api.data.AppSettings
import api.utils.Log
import java.lang.ref.WeakReference

class AppLogger : Log() {

    @Volatile
    private var ctxRef = WeakReference<Context>(null)

    override fun init(ctx: Any?) {
        ctxRef = WeakReference(ctx as Context)
    }

    override fun logi(message: String): String {
        android.util.Log.i(tag, message)
        super.logi(message)
        return ""
    }

    override fun loge(message: String): String {
        android.util.Log.e(tag, message)
        super.loge(message)
        return ""
    }

    override fun logd(message: String): String {
        if (AppSettings.isDebugEnabled) {
            android.util.Log.d(tag, message)
            super.logd(message)
        }
        return ""
    }

    override fun getPID(): Long {
        return android.os.Process.myPid().toLong()
    }

    override fun logMemoryUsage(params: Any?) {
        require(params != null) { "Param mast not be null" }

        val context = params as Context
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        // Get the limit in Megabytes
        val maxHeapBytes = activityManager.memoryClass
        val maxLargeHeapBytes = activityManager.largeMemoryClass

        val runtime = Runtime.getRuntime()

        // 1. The hard limit the VM will attempt to use (corresponds to memoryClass)
        val maxMemory = runtime.maxMemory()

        // 2. The total memory currently allocated for the JVM heap (grows and shrinks dynamically)
        val totalMemory = runtime.totalMemory()

        // 3. The unused memory within the currently allocated totalMemory
        val freeMemory = runtime.freeMemory()

        // 4. THE MAGIC FORMULA: Exactly how much memory your app is actively holding right now
        val currentUsedHeapBytes = totalMemory - freeMemory

        android.util.Log.d(
            tag,
            "MEMORY INFO: { \n" +
                "maxLargeHeap = $maxLargeHeapBytes Mb \n" +
                "maxHeap = $maxHeapBytes Mb \n " +
                "maxMemory = $maxMemory, bytes \n" +
                "totalMemory = $totalMemory, bytes \n" +
                "freeMemory = $freeMemory, bytes \n" +
                "currentUsedHeap = $currentUsedHeapBytes bytes \n }",
        )
    }

    override suspend fun hasLogFile(): Boolean {
        val value = AppSettings.logfilePath
        if (value.isEmpty()) return false
        var available = true
        try {
            val uri = Uri.parse(value)
            val context = ctxRef.get()
            context!!.contentResolver.openOutputStream(uri, "wa")?.use { outputStream ->
                outputStream.write(" ".toByteArray())
            }
        } catch (e: Exception) {
            android.util.Log.e(tag, "hasLogFile() Failed: '$e'")
            available = false
        }
        return available
    }

    override fun logToFile(message: String) {
        if (AppSettings.logfilePath.isEmpty()) return
        if (AppSettings.isDebugEnabled) {
            try {
                val context = ctxRef.get()
                val uri = Uri.parse(AppSettings.logfilePath)
                context?.contentResolver?.openOutputStream(uri, "wa")?.use { outputStream ->
                    outputStream.write("$message\n".toByteArray())
                }
            } catch (e: Exception) {
                android.util.Log.e(tag, "logToFile() Failed to write logs: '$e'")
            }
        }
    }

}
