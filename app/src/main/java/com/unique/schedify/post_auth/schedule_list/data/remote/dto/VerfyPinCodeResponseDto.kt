package com.unique.schedify.post_auth.schedule_list.data.remote.dto


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.unique.schedify.post_auth.schedule_list.data.remote.dto.VerifyPinCodeResponseDto.VerifyPinCodeResponseDtoItem
import kotlinx.parcelize.Parcelize

class VerifyPinCodeResponseDto : ArrayList<VerifyPinCodeResponseDtoItem>(){
    @Parcelize
    data class VerifyPinCodeResponseDtoItem(
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
            @SerializedName("DeliveryStatus")
            val deliveryStatus: String?,
            @SerializedName("Description")
            val description: String?,
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
    }
}