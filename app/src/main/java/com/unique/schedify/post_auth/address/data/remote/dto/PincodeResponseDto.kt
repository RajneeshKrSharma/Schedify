package com.unique.schedify.post_auth.address.data.remote.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PincodeResponseDto(
    @SerializedName("Message")
    val message: String?,
    @SerializedName("PostOffice")
    val postOffice: List<PostOffice?>?,
    @SerializedName("Status")
    val status: String?
) : Parcelable {
    @Parcelize
    data class PostOffice(
        @SerializedName("Block")
        val block: String?,
        @SerializedName("BranchType")
        val branchType: String?,
        @SerializedName("Circle")
        val circle: String?,
        @SerializedName("Country")
        val country: String?,
        @SerializedName("District")
        val district: String?,
        @SerializedName("Division")
        val division: String?,
        @SerializedName("Name")
        val name: String?,
        @SerializedName("Pincode")
        val pinCode: String?,
        @SerializedName("Region")
        val region: String?,
        @SerializedName("State")
        val state: String?
    ) : Parcelable

    companion object {
        fun default() = PincodeResponseDto(
            message = "",
            postOffice = emptyList(),
            status = ""
        )
    }
}