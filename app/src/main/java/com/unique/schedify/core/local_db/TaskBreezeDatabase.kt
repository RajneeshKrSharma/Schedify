package com.unique.schedify.core.local_db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.unique.schedify.pre_auth.pre_auth_loading.data.local.AppTourDao
import com.unique.schedify.pre_auth.pre_auth_loading.data.local.AppTourEntity

@Database(
    entities = [AppTourEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SchedifyDatabase: RoomDatabase() {
    abstract val dao: AppTourDao
}