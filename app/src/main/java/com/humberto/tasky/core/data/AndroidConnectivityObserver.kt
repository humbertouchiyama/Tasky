package com.humberto.tasky.core.data

import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import com.humberto.tasky.core.domain.ConnectivityObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

class AndroidConnectivityObserver @Inject constructor(
    private val connectivityManager: ConnectivityManager
): ConnectivityObserver {

    override fun startObserving(): Flow<ConnectivityObserver.ConnectivityStatus> {
        return callbackFlow<ConnectivityObserver.ConnectivityStatus> {
            val callback = object : NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    launch {
                        send(ConnectivityObserver.ConnectivityStatus.Available)
                    }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    launch {
                        send(ConnectivityObserver.ConnectivityStatus.Lost)
                    }
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    launch {
                        send(ConnectivityObserver.ConnectivityStatus.Unavailable)
                    }
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    val connected = networkCapabilities.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_VALIDATED
                    )
                    launch {
                        send(if(connected)
                            ConnectivityObserver.ConnectivityStatus.Available else
                                ConnectivityObserver.ConnectivityStatus.Unavailable
                        )
                    }
                }
            }

            connectivityManager.registerDefaultNetworkCallback(callback)

            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
                }
        }.distinctUntilChanged()
    }
}