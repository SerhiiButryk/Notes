package api

import kotlinx.coroutines.flow.Flow

data class NetStateInfo(val networkIsAvailable: Boolean)

interface NetStateManager {

    suspend fun isNetworkAvailable(): Boolean

    fun observerChanges(): Flow<NetStateInfo>
}