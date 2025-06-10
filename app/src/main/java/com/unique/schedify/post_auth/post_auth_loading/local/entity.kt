package com.unique.schedify.post_auth.post_auth_loading.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class UserMappedWeatherStatus(
    @PrimaryKey val id: Int? = null,
    @Embedded(prefix = "schedule_")
    val scheduleItem: ScheduleItem? = null,
    val uniqueKey: String? = null,
    val pinCode: String? = null,
    val forecastTime: String? = null,
    val timeStamp: Long? = null,
    val weatherType: String? = null,
    val weatherDescription: String? = null,
    val temperatureCelsius: Double? = null,
    val humidityPercent: Int? = null,
    val updatedCount: Int? = null,
    val notifyCount: Int? = null,
    val nextNotifyIn: String? = null,
    val nextNotifyAt: String? = null,
    val notifyMedium: String? = null,
    val isActive: Boolean? = null,
    val lastUpdated: String? = null
)

data class ScheduleItem(
    val id: Int? = null,
    val dateTime: String? = null,
    val title: String? = null,
    val lastScheduleOn: String? = null,
    val isWeatherNotifyEnabled: Boolean? = null,
    val isItemPinned: Boolean? = null,
    val subTitle: String? = null,
    val isArchived: Boolean? = null,
    val priority: Int? = null,
    val userId: Int? = null,
    val googleAuthUserId: String? = null
)