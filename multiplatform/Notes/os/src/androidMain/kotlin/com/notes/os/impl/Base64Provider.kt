package com.notes.os.impl

import android.util.Base64
import api.security.Base64Operations

class Base64Provider : Base64Operations {
    override fun encode(input: ByteArray): ByteArray = Base64.encode(input, Base64.NO_WRAP)

    override fun decode(input: String): ByteArray = Base64.decode(input, Base64.NO_WRAP)
}