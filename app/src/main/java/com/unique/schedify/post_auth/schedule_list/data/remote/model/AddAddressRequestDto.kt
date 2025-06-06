package com.unique.schedify.post_auth.schedule_list.data.remote.model


import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddAddressRequestDto(
    @SerializedName("address")
    val address: String,
    @SerializedName("pincode")
    val pinCode: String
): Parcelable