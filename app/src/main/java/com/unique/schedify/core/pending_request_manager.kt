package com.unique.schedify.core

import android.util.Log
import com.unique.schedify.core.network.utility.ErrorCodes
import com.unique.schedify.core.network.utility.NetworkErrorEmitter
import com.unique.schedify.core.network.utility.getErrorMsgFromStatusCode
import com.unique.schedify.core.network.utility.getUserFriendlyMessage
import com.unique.schedify.core.util.ApiResponseResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

// Generic interface for API requests
interface ApiUseCase<T, in U> {  // T is the return type, U is the argument type
    suspend fun execute(args: U? = null): T

    suspend fun <T> catchWrapper(apiCall: suspend () -> Response<T>): ApiResponseResource<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                response.body()?.let { data ->
                    ApiResponseResource.Success(data)
                } ?: handleErrorResponse(response)
            } else {
                handleErrorResponse(response)
            }
        } catch (e: Exception) {
            ApiResponseResource.Error(getUserFriendlyMessage(e))
        }
    }

    private fun <T> handleErrorResponse(response: Response<T>): ApiResponseResource<T> {
        return if (response.code() in ErrorCodes.getApiErrorUiHandlingCodeList()) {
            NetworkErrorEmitter.emitError(response.code())
            ApiResponseResource.Error(getErrorMsgFromStatusCode(response.code()))
        } else {
            ApiResponseResource.Error(
                response.errorBody()?.string() ?: "Something went wrong with error body"
            )
        }
    }
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
