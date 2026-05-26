package com.notes.os.impl

import android.app.ActivityManager
import android.content.Context

class AppLogger : PlatformLog() {

    override fun logi(message: String) {
        android.util.Log.i("Notes", message)
    }

    override fun loge(message: String) {
        android.util.Log.e("Notes", message)
    }

    override fun logDebug(message: String) {
        android.util.Log.d("Notes", message)
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

        android.util.Log.d("Notes", "MEMORY INFO: { \n" +
                "maxLargeHeap = $maxLargeHeapBytes Mb \n" +
                "maxHeap = $maxHeapBytes Mb \n " +
                "maxMemory = $maxMemory, bytes \n" +
                "totalMemory = $totalMemory, bytes \n" +
                "freeMemory = $freeMemory, bytes \n" +
                "currentUsedHeap = $currentUsedHeapBytes bytes \n }")
    }
}