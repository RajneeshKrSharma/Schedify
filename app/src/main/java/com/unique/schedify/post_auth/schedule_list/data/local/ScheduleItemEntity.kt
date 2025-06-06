package com.unique.schedify.post_auth.schedule_list.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedule_items")
data class ScheduleItemEntity(
    @PrimaryKey val id: Int,
    val dateTime: String, // Original schedule time
    val title: String,
    val pincode: String,
    var nextNotifyAt: String? // This can be a dateTime or "NOTIFY BLOCKED: 0.00"
)
