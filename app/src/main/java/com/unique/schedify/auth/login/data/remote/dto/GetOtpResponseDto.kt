package com.unique.schedify.auth.login.data.remote.dto


import com.google.gson.annotations.SerializedName

data class GetOtpResponseDto(
    @SerializedName("data")
    val `data`: Data?,
    @SerializedName("message")
    val message: String?
) {
    data class Data(
        @SerializedName("message")
        val message: String?,
        @SerializedName("otp")
        val otp: Int?
    )

    companion object {
        val empty = GetOtpResponseDto(null, null)
    }
}