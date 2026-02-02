package api

interface StorageOperations {
    suspend fun save(
        value: String,
        key: String,
    ): Boolean

    suspend fun get(key: String): String
    suspend fun clearAll()
}
