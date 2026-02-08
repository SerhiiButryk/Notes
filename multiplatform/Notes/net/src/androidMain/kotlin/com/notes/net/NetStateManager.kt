package com.notes.net

import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import api.NetStateInfo
import api.NetStateManager
import api.PlatformAPIs.logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

class NetStateManager(private val connectivityManager: ConnectivityManager) : NetStateManager {

    private val tag = "NetStateManager"
    private val newtStateInfoChannel = Channel<NetStateInfo>(capacity = Channel.CONFLATED)
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    init {
        addCallback()
    }

    override suspend fun isNetworkAvailable(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        return hasAvailableNetwork(connectivityManager.getNetworkCapabilities(network))
    }

    private fun addCallback() {

        connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                logger.logi("$tag::onAvailable")
                updateNetworkState(network)
            }

            override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
                logger.logi("$tag::onBlockedStatusChanged")
                updateNetworkState(network)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                logger.logi("$tag::onCapabilitiesChanged")
                updateNetworkState(network)
            }

            override fun onLinkPropertiesChanged(
                network: Network,
                linkProperties: LinkProperties
            ) {
                logger.logi("$tag::onLinkPropertiesChanged")
                updateNetworkState(network)
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                logger.logi("$tag::onLosing")
                updateNetworkState(network)
            }

            override fun onLost(network: Network) {
                logger.logi("$tag::onLost")
                scope.launch {
                    newtStateInfoChannel.send(NetStateInfo(networkIsAvailable = false))
                }
            }

            override fun onReserved(networkCapabilities: NetworkCapabilities) {
                logger.logi("$tag::onReserved")
            }

            override fun onUnavailable() {
                logger.logi("$tag::onUnavailable")
                scope.launch {
                    newtStateInfoChannel.send(NetStateInfo(networkIsAvailable = false))
                }
            }
        })
    }

    override fun observerChanges(): Flow<NetStateInfo> {
        return channelFlow {
            for (info in newtStateInfoChannel) {
                send(info)
            }
        }
    }

    private fun updateNetworkState(network: Network) {
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        val hasNetwork = hasAvailableNetwork(capabilities)
        scope.launch {
            newtStateInfoChannel.send(NetStateInfo(networkIsAvailable = hasNetwork))
        }
    }

    private fun hasAvailableNetwork(capabilities: NetworkCapabilities?): Boolean {
        if (capabilities == null) { return false }
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

}