package api.security

interface DerivedKeyOperations {
    fun generatePDKey(
        input: String,
        salt: ByteArray,
    ): String

    fun generateSalt(): ByteArray
}