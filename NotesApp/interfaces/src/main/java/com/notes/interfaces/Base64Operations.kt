package com.notes.interfaces

interface Base64Operations {
    fun encode(byteArray: ByteArray): String
    fun decode(token: String): ByteArray
}
