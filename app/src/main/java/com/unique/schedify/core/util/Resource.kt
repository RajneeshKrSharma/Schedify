package com.unique.schedify.core.util

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?): Resource<T>(data)
    class Error<T>(message: String, data: T? = null): Resource<T>(data, message)
    class Loading<T>(): Resource<T>()
    class Default<T>(): Resource<T>()
}

sealed interface ApiResponseResource<out T> {
    data class Success<T>(val data: T) : ApiResponseResource<T>
    data class Error(val errorMessage: String) : ApiResponseResource<Nothing>
}
