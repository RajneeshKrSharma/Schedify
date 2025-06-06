package com.unique.schedify.post_auth.schedule_list.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ScheduleItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: ScheduleItemEntity)

    @Query("SELECT * FROM schedule_items")
    suspend fun getAllItems(): List<ScheduleItemEntity>

    @Query("SELECT * FROM schedule_items WHERE id = :id")
    suspend fun getItemById(id: Int): ScheduleItemEntity?

    @Query("SELECT * FROM schedule_items WHERE nextNotifyAt <= :currentDateTime AND dateTime > :isoCurrentTime")
    fun getItemsToNotify(currentDateTime: String,isoCurrentTime: String): List<ScheduleItemEntity>

    @Update
    fun updateItem(item: ScheduleItemEntity)

    @Query("UPDATE schedule_items SET title = :title")
    fun updateTitle(title: String)

}