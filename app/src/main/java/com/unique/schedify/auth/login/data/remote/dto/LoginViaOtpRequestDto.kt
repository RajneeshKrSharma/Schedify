package com.unique.schedify.auth.login.data.remote.dto


import com.google.gson.annotations.SerializedName

data class LoginViaOtpRequestDto(
    @SerializedName("emailId")
    val emailId: String?,
    @SerializedName("otp")
    val otp: String?,
    @SerializedName("fcmToken")
    val fcmToken: String?
) {
    companion object {
        fun default() = LoginViaOtpRequestDto(
            emailId = null,
            otp = null,
            fcmToken = null
        )
    }
}