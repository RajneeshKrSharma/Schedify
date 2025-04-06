package com.unique.schedify.auth.login.data.remote.dto


import com.google.gson.annotations.SerializedName

data class OAuthAccessTokenRequestDto(
    @SerializedName("client_id")
    val clientId: String?,

    @SerializedName("client_secret")
    val clientSecret: String?,

    @SerializedName("code")
    val code: String?,

    @SerializedName("grant_type")
    val grantType: String?,
) {
    companion object {
        fun empty() = OAuthAccessTokenRequestDto(
            clientId = "",
            clientSecret = "",
            code = null,
            grantType = "authorization_code"
        )
    }
}