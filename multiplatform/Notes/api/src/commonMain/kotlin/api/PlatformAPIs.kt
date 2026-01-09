package api

/**
 * Platform specific operations for other components
 */
object PlatformAPIs {
    lateinit var base64: Base64Operations
    lateinit var storage: StorageOperations
    lateinit var derivedKey: DerivedKeyOperations
    lateinit var logger: Log
    lateinit var crypto: CryptoOperations
}
