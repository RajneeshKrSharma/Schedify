package com.unique.schedify.core

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

// Generic interface for API requests
interface ApiUseCase<T, in U> {  // T is the return type, U is the argument type
    suspend fun execute(args: U? = null): T
}

// Data model to hold the request and retry status
data class KModel<T, U>(
    val isAutoRetry: Boolean,
    val request: ApiUseCase<T, U>
)

// Manager for handling pending requests
@Singleton
class PendingRequestManager @Inject constructor(
    private var connectivityChecker: ConnectivityChecker
) {

    private val requestPool: MutableList<KModel<*, *>> = mutableListOf()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            connectivityChecker.isConnected.collect { isConnected ->
                if (isConnected && requestPool.isNotEmpty()) {
                    executeRequests()
                }
            }
        }
    }

    // Method to add a request to the pool
    fun <T, U> addRequest(isAutoRetry: Boolean, request: ApiUseCase<T, U>, args: U? = null) {
        requestPool.add(KModel(isAutoRetry, request))
    }

    // Method to execute all requests in the pool
    private suspend fun executeRequests() {
        val snapshot = requestPool.toList() // Create a snapshot of the collection
        snapshot.forEach { model ->
            try {
                val result = model.request.execute()
            } catch (e: Exception) {
                Log.e("SchedifyExceptions", "Request execution failed: ${e.message}")
            }
        }
    }
}
