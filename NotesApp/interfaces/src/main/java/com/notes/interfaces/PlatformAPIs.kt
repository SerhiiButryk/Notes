package com.notes.interfaces

/**
 * Platform specific operations for modules
 */
object PlatformAPIs {
    lateinit var base64: Base64Operations
    lateinit var storage: StorageOperations
    lateinit var derivedKey: DerivedKeyOperations
}