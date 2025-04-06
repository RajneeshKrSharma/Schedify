package com.unique.schedify.auth.login.data.remote.dto


import com.google.gson.annotations.SerializedName

data class LoginViaOtpResponseDto(
    @SerializedName("data")
    val `data`: Data?,
    @SerializedName("message")
    val message: String?,
    ) {
    data class Data(
        @SerializedName("authData")
        val authData: AuthData?,
        @SerializedName("emailId")
        val emailId: String?
    ) {
        data class AuthData(
            @SerializedName("key")
            val key: String?,
            @SerializedName("user")
            val user: Int?
        )
    }

    companion object {
        val empty = LoginViaOtpResponseDto(null, null)
    }
}