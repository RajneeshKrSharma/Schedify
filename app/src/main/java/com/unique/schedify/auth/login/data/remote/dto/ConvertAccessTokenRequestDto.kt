package com.unique.schedify.auth.login.data.remote.dto


import com.google.gson.annotations.SerializedName

data class ConvertAccessTokenRequestDto(
    @SerializedName("client_id")
    val clientId: String?,

    @SerializedName("client_secret")
    val clientSecret: String?,

    @SerializedName("token")
    val token: String?,

    @SerializedName("grant_type")
    val grantType: String?,

    @SerializedName("backend")
    val backend: String?,
) {
    companion object {
        fun empty() = ConvertAccessTokenRequestDto(
            clientId = "",
            clientSecret = "",
            token = null,
            grantType = "convert_token",
            backend = "google-oauth2"
        )
    }
}