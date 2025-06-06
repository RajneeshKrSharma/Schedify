package com.unique.schedify.post_auth.schedule_list.data.remote.dto


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WeatherStatusResponseDto(
    @SerializedName("weather_notify_details")
    @Expose
    val weatherNotifyDetails: List<WeatherNotifyDetails?>,
    @SerializedName("weather_status_images")
    @Expose
    val weatherStatusImages: List<WeatherStatusImage?>?
) {
    data class WeatherNotifyDetails(
        @SerializedName("forecast_time")
        @Expose
        val forecastTime: String?,
        @SerializedName("humidity_percent")
        @Expose
        val humidityPercent: Int?,
        @SerializedName("id")
        @Expose
        val id: Int?,
        @SerializedName("isActive")
        @Expose
        val isActive: Boolean?,
        @SerializedName("last_updated")
        @Expose
        val lastUpdated: String?,
        @SerializedName("next_notify_at")
        @Expose
        val nextNotifyAt: String?,
        @SerializedName("next_notify_in")
        @Expose
        val nextNotifyIn: String?,
        @SerializedName("notify_count")
        @Expose
        val notifyCount: Int?,
        @SerializedName("notify_medium")
        @Expose
        val notifyMedium: String?,
        @SerializedName("pincode")
        @Expose
        val pincode: String?,
        @SerializedName("scheduleItem")
        @Expose
        val scheduleItem: ScheduleItem?,
        @SerializedName("temperature_celsius")
        @Expose
        val temperatureCelsius: Double?,
        @SerializedName("timeStamp")
        @Expose
        val timeStamp: Int?,
        @SerializedName("unique_key")
        @Expose
        val uniqueKey: String?,
        @SerializedName("updated_count")
        @Expose
        val updatedCount: Int?,
        @SerializedName("weatherDescription")
        @Expose
        val weatherDescription: String?,
        @SerializedName("weatherType")
        @Expose
        val weatherType: String?
    ) {
        data class ScheduleItem(
            @SerializedName("attachments")
            @Expose
            val attachments: List<Any?>?,
            @SerializedName("dateTime")
            @Expose
            val dateTime: String?,
            @SerializedName("google_auth_user_id")
            @Expose
            val googleAuthUserId: Any?,
            @SerializedName("id")
            @Expose
            val id: Int?,
            @SerializedName("isArchived")
            @Expose
            val isArchived: Boolean?,
            @SerializedName("isItemPinned")
            @Expose
            val isItemPinned: Boolean?,
            @SerializedName("isWeatherNotifyEnabled")
            @Expose
            val isWeatherNotifyEnabled: Boolean?,
            @SerializedName("lastScheduleOn")
            @Expose
            val lastScheduleOn: String?,
            @SerializedName("priority")
            @Expose
            val priority: Int?,
            @SerializedName("subTitle")
            @Expose
            val subTitle: String?,
            @SerializedName("title")
            @Expose
            val title: String?,
            @SerializedName("user_id")
            @Expose
            val userId: Int?
        )
    }

    data class WeatherStatusImage(
        @SerializedName("id")
        @Expose
        val id: Int?,
        @SerializedName("status")
        @Expose
        val status: String?,
        @SerializedName("url")
        @Expose
        val url: String?
    )
}