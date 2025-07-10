package com.unique.schedify.post_auth.schedule_list.repository

import com.unique.schedify.post_auth.schedule_list.remote.ScheduleListApis
import com.unique.schedify.post_auth.schedule_list.remote.dto.ScheduleListResponseDto
import retrofit2.Response
import javax.inject.Inject

class ScheduleListRepository @Inject constructor(
     private val api: ScheduleListApis
) {

    suspend fun getScheduleList(): Response<ScheduleListResponseDto> {
        return api.getScheduleList()
    }

}