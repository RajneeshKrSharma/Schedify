package com.unique.schedify.core.network.utility

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object NetworkErrorEmitter {
    private val _networkErrors = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val networkErrors = _networkErrors.asSharedFlow()

    fun emitError(error: Int) {
        _networkErrors.tryEmit(error)
    }
}