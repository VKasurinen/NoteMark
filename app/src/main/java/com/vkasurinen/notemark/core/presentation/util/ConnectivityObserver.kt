package com.vkasurinen.notemark.core.presentation.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

interface ConnectivityObserver {
    val connectivity: Flow<Boolean>
}

class ConnectivityObserverImpl(
    private val context: Context
) : ConnectivityObserver {

    override val connectivity: Flow<Boolean> = callbackFlow {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        fun snapshot(): Boolean {
            val net = cm.activeNetwork
            val caps = cm.getNetworkCapabilities(net)
            return caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        }

        trySend(snapshot())

        val cb = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                trySend(true).isSuccess
            }

            override fun onLost(network: Network) {
                trySend(false).isSuccess
            }

            override fun onCapabilitiesChanged(
                network: Network,
                caps: NetworkCapabilities
            ) {
                val hasNet = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                trySend(hasNet).isSuccess
            }
        }

        cm.registerNetworkCallback(NetworkRequest.Builder().build(), cb)
        awaitClose { cm.unregisterNetworkCallback(cb) }
    }.distinctUntilChanged()
}
