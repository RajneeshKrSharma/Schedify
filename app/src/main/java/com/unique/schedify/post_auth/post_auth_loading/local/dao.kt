package com.unique.schedify.post_auth.post_auth_loading.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserMappedWeatherStatusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserMappedWeatherStatusDaoData(
        userMappedWeatherStatusDataList: List<UserMappedWeatherStatus>
    )

    @Query("DELETE FROM UserMappedWeatherStatus")
    suspend fun deleteUserMappedWeatherStatusData()
}