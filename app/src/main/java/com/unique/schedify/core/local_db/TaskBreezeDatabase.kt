package com.unique.schedify.core.local_db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.unique.schedify.post_auth.post_auth_loading.local.UserMappedWeatherStatus
import com.unique.schedify.post_auth.post_auth_loading.local.UserMappedWeatherStatusDao
import com.unique.schedify.pre_auth.pre_auth_loading.data.local.AppTourEntity
import com.unique.schedify.pre_auth.pre_auth_loading.data.local.HomeCarouselBannerEntity
import com.unique.schedify.pre_auth.pre_auth_loading.data.local.HomeCellDetailEntity
import com.unique.schedify.pre_auth.pre_auth_loading.data.local.PreAuthDao

@Database(
    entities = [
        AppTourEntity::class,
        UserMappedWeatherStatus::class,
        HomeCarouselBannerEntity::class,
        HomeCellDetailEntity::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class SchedifyDatabase: RoomDatabase() {
    abstract val preAuthDao: PreAuthDao
    abstract val userMappedWeatherStatusDao: UserMappedWeatherStatusDao
}