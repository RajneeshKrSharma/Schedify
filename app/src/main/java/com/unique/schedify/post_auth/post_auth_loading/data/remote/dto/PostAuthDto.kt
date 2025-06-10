package com.unique.schedify.post_auth.post_auth_loading.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PostAuthDto(
    @SerializedName("address_detail")
    val addressDetail: AddressDetail?
)

data class AddressDetail(
    @SerializedName("id")
    val id: Int?,

    @SerializedName("pincode")
    val pincode: String?,

    @SerializedName("address")
    val address: String?,

    @SerializedName("user_id")
    val userId: Int?
)