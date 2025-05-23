package com.notes.app.security

import android.util.Base64
import com.notes.api.Base64Operations
import javax.inject.Inject

class Base64Provider @Inject constructor() : Base64Operations {

    override fun encode(byteArray: ByteArray): String {
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    override fun decode(token: String): ByteArray {
        return Base64.decode(token, Base64.NO_WRAP)
    }
}