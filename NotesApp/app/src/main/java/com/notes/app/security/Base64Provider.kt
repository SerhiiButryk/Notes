package com.notes.app.security

import android.util.Base64
import com.notes.interfaces.Base64Operations

class Base64Provider : Base64Operations {

    override fun encode(byteArray: ByteArray): String {
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    override fun decode(token: String): ByteArray {
        return Base64.decode(token, Base64.NO_WRAP)
    }
}