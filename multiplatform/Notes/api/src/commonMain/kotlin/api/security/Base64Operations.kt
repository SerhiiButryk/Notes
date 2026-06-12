package api.security

interface Base64Operations {
    fun encode(input: ByteArray): ByteArray

    fun decode(input: String): ByteArray
}