package com.unique.schedify.post_auth.schedule_list.data.remote.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GetWeatherStatusRequestDto(
    @SerializedName("pincode")
    @Expose
    val pincode: String?,
    @SerializedName("scheduledItemId")
    @Expose
    val scheduleItemId: String?
)