package api

import api.data.StorageOperations
import api.net.NetStateManager
import api.security.Base64Operations
import api.security.CryptoOperations
import api.security.DerivedKeyOperations
import api.utils.Log

interface OSPlatform {

    val base64: Base64Operations

    val storage: StorageOperations

    val derivedKey: DerivedKeyOperations

    val logger: Log

    val crypto: CryptoOperations

    val netStateManager: NetStateManager
}

var platform: OSPlatform? = null

fun Platform(): OSPlatform {
    val pl = platform
    requireNotNull(pl) { "Platform is not set" }
    return pl
}