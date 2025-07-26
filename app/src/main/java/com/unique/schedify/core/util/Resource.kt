package com.unique.schedify.core.util

sealed class Resource<T>(val data: T? = null, val message: String? = null, val stringResourceId: Int? = null) {
    class Success<T>(data: T?): Resource<T>(data)
    class Error<T>(message: String? = null, data: T? = null, stringResourceId: Int? = null): Resource<T>(data, message, stringResourceId)
    class Loading<T>(): Resource<T>()
    class Default<T>(): Resource<T>()
}

sealed interface ApiResponseResource<out T> {
    data class Success<T>(val data: T) : ApiResponseResource<T>
    data class Error(val errorMessage: String) : ApiResponseResource<Nothing>
}
