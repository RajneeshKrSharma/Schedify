package com.unique.schedify.post_auth.address.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AddAddressResponseDto(
    @SerializedName("pincode")
    val pincode: String?,
    @SerializedName("address")
    val address: String?,
)