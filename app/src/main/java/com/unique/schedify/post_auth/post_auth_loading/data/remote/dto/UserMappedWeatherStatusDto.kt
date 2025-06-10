package com.unique.schedify.post_auth.post_auth_loading.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserMappedWeatherStatusDto(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("scheduleItem")
    val scheduleItem: ScheduleItemDto? = null,

    @SerializedName("unique_key")
    val uniqueKey: String? = null,

    @SerializedName("pincode")
    val pincode: String? = null,

    @SerializedName("forecast_time")
    val forecastTime: String? = null,

    @SerializedName("timeStamp")
    val timeStamp: Long? = null,

    @SerializedName("weatherType")
    val weatherType: String? = null,

    @SerializedName("weatherDescription")
    val weatherDescription: String? = null,

    @SerializedName("temperature_celsius")
    val temperatureCelsius: Double? = null,

    @SerializedName("humidity_percent")
    val humidityPercent: Int? = null,

    @SerializedName("updated_count")
    val updatedCount: Int? = null,

    @SerializedName("notify_count")
    val notifyCount: Int? = null,

    @SerializedName("next_notify_in")
    val nextNotifyIn: String? = null,

    @SerializedName("next_notify_at")
    val nextNotifyAt: String? = null,

    @SerializedName("notify_medium")
    val notifyMedium: String? = null,

    @SerializedName("isActive")
    val isActive: Boolean? = null,

    @SerializedName("last_updated")
    val lastUpdated: String? = null
)

data class ScheduleItemDto(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("dateTime")
    val dateTime: String? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("lastScheduleOn")
    val lastScheduleOn: String? = null,

    @SerializedName("isWeatherNotifyEnabled")
    val isWeatherNotifyEnabled: Boolean? = null,

    @SerializedName("isItemPinned")
    val isItemPinned: Boolean? = null,

    @SerializedName("subTitle")
    val subTitle: String? = null,

    @SerializedName("isArchived")
    val isArchived: Boolean? = null,

    @SerializedName("priority")
    val priority: Int? = null,

    @SerializedName("user_id")
    val userId: Int? = null,

    @SerializedName("google_auth_user_id")
    val googleAuthUserId: String? = null,

    @SerializedName("attachments")
    val attachments: List<String>? = emptyList()
)