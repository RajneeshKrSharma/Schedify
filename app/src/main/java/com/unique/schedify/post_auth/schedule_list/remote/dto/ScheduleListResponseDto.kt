package com.unique.schedify.post_auth.schedule_list.remote.dto


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScheduleListResponseDto(
    @SerializedName("data")
    var data: List<Data?>?
): Parcelable {
    @Parcelize
    data class Data(
        @SerializedName("attachments")
        var attachments: List<String?>?,
        @SerializedName("dateTime")
        val dateTime: String?,
        @SerializedName("google_auth_user_id")
        val googleAuthUserId: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("isArchived")
        val isArchived: Boolean?,
        @SerializedName("isItemPinned")
        val isItemPinned: Boolean?,
        @SerializedName("lastScheduleOn")
        val lastScheduleOn: String?,
        @SerializedName("priority")
        val priority: Int?,
        @SerializedName("subTitle")
        val subTitle: String?,
        @SerializedName("title")
        val title: String?,
        @SerializedName("user_id")
        val userId: Int?,
        @SerializedName("isWeatherNotifyEnabled")
        val isWeatherNotifyEnabled: Boolean? = false
    ): Parcelable
}