package com.notes.app.log

import api.Log

class AppLogger : Log {
    override fun logi(message: String) {
        android.util.Log.i("Notes", message)
    }

    override fun loge(message: String) {
        android.util.Log.e("Notes", message)
    }
}
