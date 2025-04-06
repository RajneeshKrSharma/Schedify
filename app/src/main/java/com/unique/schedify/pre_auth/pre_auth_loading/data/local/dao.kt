package com.unique.schedify.pre_auth.pre_auth_loading.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AppTourDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppTourData(
        appTourEntity: List<AppTourEntity>
    )

    @Query("DELETE FROM apptourentity")
    suspend fun deleteAppTourData()
}