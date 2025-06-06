package com.unique.schedify.post_auth.schedule_list.data.remote.dto


import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddAddressResponseDto(
    @SerializedName("address")
    val address: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("pincode")
    val pinCode: String?,
    @SerializedName("user_id")
    val userId: Int?
): Parcelable