package com.unique.schedify.core.network.utility

fun getUserFriendlyMessage(exception: Throwable): String {
    return when (exception) {

        // Network-related issues
        is java.net.UnknownHostException ->
            "No internet connection. Please check your network settings."
        is java.net.SocketTimeoutException ->
            "Request timed out. Please try again later."
        is java.net.ConnectException ->
            "Unable to connect to the server. Please try again."
        is java.net.NoRouteToHostException ->
            "Network route to server is unavailable. Check your connection."
        is java.net.ProtocolException ->
            "A network protocol error occurred. Please try again."

        // SSL-related issues
        is javax.net.ssl.SSLHandshakeException ->
            "Secure connection failed. The server might be misconfigured."
        is javax.net.ssl.SSLPeerUnverifiedException ->
            "Failed to verify server's identity. Please try again later."

        // HTTP-specific errors (Retrofit)
        is retrofit2.HttpException -> {
            val code = exception.code()
            getErrorMsgFromStatusCode(code)
        }

        // Parsing & format-related errors
        is com.google.gson.JsonSyntaxException,
        is org.json.JSONException,
        is java.text.ParseException ->
            "Unexpected data format received from the server."

        // IO errors
        is java.io.EOFException ->
            "The connection was interrupted unexpectedly."
        is java.io.IOException ->
            "A network error occurred. Please check your internet connection."

        // Application-specific errors
        is kotlin.UninitializedPropertyAccessException ->
            "A required property was not initialized properly."
        is IllegalStateException ->
            exception.message ?: "An invalid state was encountered in the app."
        is IllegalArgumentException ->
            exception.message ?: "Invalid input provided."

        // Fallback for any other unhandled exceptions
        else -> exception.localizedMessage ?: "An unexpected error occurred. Please try again."
    }
}

fun getErrorMsgFromStatusCode(code: Int): String {
    return when (code) {
        400 -> "Bad request. Please check the submitted information."
        401 -> "Unauthorized. Please login and try again."
        403 -> "Access denied. You don’t have permission to perform this action."
        404 -> "The requested resource could not be found."
        500 -> "Server error. Please try again later."
        502 -> "Bad gateway. There’s a problem with the server."
        503 -> "Server is currently unavailable. Please try again later."
        504 -> "Gateway timeout. The server is taking too long to respond."
        else -> "HTTP error"
    }

}
