package com.unique.schedify.core.local_db

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.unique.schedify.post_auth.schedule_list.data.local.ScheduleItemDao
import com.unique.schedify.post_auth.schedule_list.data.local.ScheduleItemEntity
import com.unique.schedify.pre_auth.pre_auth_loading.data.local.AppTourDao
import com.unique.schedify.pre_auth.pre_auth_loading.data.local.AppTourEntity
import java.time.LocalDateTime
import java.time.ZoneOffset

@Database(
    entities = [AppTourEntity::class, ScheduleItemEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SchedifyDatabase: RoomDatabase() {
    abstract val dao: AppTourDao
    abstract val scheduleItemDao: ScheduleItemDao
}


class Converters {
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): Long? {
        return dateTime?.toInstant(ZoneOffset.UTC)?.toEpochMilli()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toLocalDateTime(timestamp: Long?): LocalDateTime? {
        return timestamp?.let {
            LocalDateTime.ofEpochSecond(it / 1000, 0, ZoneOffset.UTC)
        }
    }
}