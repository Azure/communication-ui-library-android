// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.presentation.manager

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.azure.android.communication.ui.chat.redux.Dispatch
import com.azure.android.communication.ui.chat.redux.action.NetworkAction

internal class NetworkManager(dispatch: Dispatch) {
    private var connectivityManager: ConnectivityManager? = null

    fun start(context: Context) {
        val networkRequest =
            NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build()
        connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager?.requestNetwork(networkRequest, networkCallback)
    }

    fun stop() {
        connectivityManager?.unregisterNetworkCallback(networkCallback)
    }

    private val networkCallback =
        object : ConnectivityManager.NetworkCallback() {
            // network is available for use
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                dispatch(NetworkAction.Connected())
            }

            // lost network connection
            override fun onLost(network: Network) {
                super.onLost(network)
                dispatch(NetworkAction.Disconnected())
            }
        }
}
