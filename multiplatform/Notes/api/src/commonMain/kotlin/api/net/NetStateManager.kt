package api.net

import kotlinx.coroutines.flow.Flow

data class NetStateInfo(val networkIsAvailable: Boolean)

interface NetStateManager {

    suspend fun isNetworkAvailable(): Boolean

    fun startObserver()

    fun observerChanges(): Flow<NetStateInfo>
}