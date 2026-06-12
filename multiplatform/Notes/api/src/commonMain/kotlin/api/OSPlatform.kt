package api

import api.data.StorageOperations
import api.net.HttpClient
import api.net.NetStateManager
import api.repo.BaseRepo
import api.security.Base64Operations
import api.security.CryptoOperations
import api.utils.Log

interface OSPlatform {

    val base64: Base64Operations

    val storage: StorageOperations

    val logger: Log

    val crypto: CryptoOperations

    val netStateManager: NetStateManager

    val appRepo: BaseRepo

    val httpClient: HttpClient
}

var platform: OSPlatform? = null

fun Platform(): OSPlatform {
    val pl = platform
    requireNotNull(pl) { "Platform hasn't been installed" }
    return pl
}