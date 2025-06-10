package com.unique.schedify.pre_auth.pre_auth_loading.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PreAuthDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppTourData(
        appTourEntity: List<AppTourEntity>
    )

    @Query("DELETE FROM apptourentity")
    suspend fun deleteAppTourData()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomeCarouselBannerData(
        homeCarouselBannerEntityList: List<HomeCarouselBannerEntity>
    )

    @Query("Select * from HomeCarouselBanner")
    suspend fun getHomeCarouselBannerData(): List<HomeCarouselBannerEntity>

    @Query("DELETE FROM HomeCarouselBanner")
    suspend fun deleteHomeCarouselBannerData()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomeCellDetail(
        homeCellEntityList: List<HomeCellDetailEntity>
    )

    @Query("Select * from HomeCellDetail")
    suspend fun getHomeCellDetail(): List<HomeCellDetailEntity>

    @Query("DELETE FROM HomeCellDetail")
    suspend fun deleteHomeCellDetail()
}