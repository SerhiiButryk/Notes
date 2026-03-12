package com.notes.os.impl

import api.security.Base64Operations
import java.util.Base64

class Base64Provider : Base64Operations {
    override fun encode(input: ByteArray): ByteArray = Base64.getEncoder().encode(input)

    override fun decode(input: String): ByteArray = Base64.getDecoder().decode(input)
}
