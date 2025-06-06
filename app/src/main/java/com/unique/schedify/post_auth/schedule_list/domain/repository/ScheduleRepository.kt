package com.unique.schedify.post_auth.schedule_list.domain.repository

import com.unique.schedify.post_auth.schedule_list.data.local.ScheduleItemDao
import com.unique.schedify.post_auth.schedule_list.data.local.ScheduleItemEntity

class ScheduleRepository(private val dao: ScheduleItemDao) {
    suspend fun insertOrUpdate(item: ScheduleItemEntity) = dao.insertOrUpdate(item)
    suspend fun getItemsToNotify( currentTimestamp: String, isoCurrentTime: String) = dao.getItemsToNotify(currentTimestamp,isoCurrentTime)
    suspend fun getItemById(id: Int) = dao.getItemById(id)
    suspend fun updateItem(item: ScheduleItemEntity) = dao.updateItem(item)
    suspend fun updateTitle(title: String) = dao.updateTitle(title)
}