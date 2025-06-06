package com.unique.schedify.post_auth.schedule_list.data.remote.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddScheduleItemRequestModel(
    @SerializedName("dateTime")
    val dateTime: String?,
    @SerializedName("files")
    val files: List<String?>?,
    @SerializedName("location")
    val location: String?,
    @SerializedName("subTitle")
    val subTitle: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("isWeatherNotifyEnabled")
    val isWeatherNotifyEnabled: Boolean? = false

) : Parcelable