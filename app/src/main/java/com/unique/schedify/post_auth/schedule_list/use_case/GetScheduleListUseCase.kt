package com.unique.schedify.post_auth.schedule_list.use_case

import com.unique.schedify.core.ApiUseCase
import com.unique.schedify.core.util.ApiResponseResource
import com.unique.schedify.post_auth.schedule_list.data.remote.dto.ScheduleListResponseDto
import com.unique.schedify.post_auth.schedule_list.domain.repository.ScheduleListRepository
import javax.inject.Inject

class GetScheduleListUseCase @Inject constructor(
    private val repository: ScheduleListRepository,
) : ApiUseCase<ApiResponseResource<ScheduleListResponseDto>, Unit>{
    override suspend fun execute(args: Unit?): ApiResponseResource<ScheduleListResponseDto> {
        val result = repository.getScheduleList()
        return (if (result.isSuccessful) {
            result.body()?.let { data ->
                ApiResponseResource.Success(data)
            } ?: ApiResponseResource.Error("")
        } else {
            ApiResponseResource.Error(result.errorBody()?.string() ?: "Something went wrong with error body")
        })
    }
}