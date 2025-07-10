package com.unique.schedify.post_auth.schedule_list.remote

import com.unique.schedify.core.network.Api.SCHEDULE_LIST
import com.unique.schedify.core.network.BaseApi
import com.unique.schedify.post_auth.schedule_list.remote.dto.ScheduleListResponseDto
import retrofit2.Response
import retrofit2.http.GET


interface ScheduleListApis : BaseApi {

    @GET(SCHEDULE_LIST)
    suspend fun getScheduleList(): Response<ScheduleListResponseDto>

}
