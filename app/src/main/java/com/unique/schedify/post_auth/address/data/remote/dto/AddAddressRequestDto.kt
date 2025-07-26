package com.unique.schedify.post_auth.address.data.remote.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddAddressRequestDto(
    @SerializedName("address")
    val address: String,
    @SerializedName("pincode")
    val pinCode: String
): Parcelable