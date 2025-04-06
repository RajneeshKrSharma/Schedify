package com.unique.schedify.core.domain

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


class InternetUINotifyUseCase {

    @Singleton // Ensures a single instance is used across the application
    class ConnectivityChecker @Inject constructor(
        @ApplicationContext context: Context
    ) {
        var onInternetAvailable: (() -> Unit)? = null
        var onInternetLost: (() -> Unit)? = null

        private val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        private val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                onInternetAvailable?.invoke()
            }

            override fun onLost(network: Network) {
                onInternetLost?.invoke()
            }
        }

        init {
            registerNetworkCallback()
        }

        private fun registerNetworkCallback() {
            connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(), networkCallback)
        }

        fun unregister() {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }
}