package com.notes.os.impl

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

}