package com.unique.schedify.core.network.utility

enum class ErrorCodes(val code: Int) {
    UNAUTHORIZED(401);

    companion object {
        fun getApiErrorUiHandlingCodeList() = ErrorCodes.entries.map { it.code }
    }
}