package com.unique.schedify.post_auth.schedule_list.use_case

import com.unique.schedify.core.ApiUseCase
import com.unique.schedify.core.util.ApiResponseResource
import com.unique.schedify.post_auth.schedule_list.data.remote.dto.AddScheduleItemDto
import com.unique.schedify.post_auth.schedule_list.data.remote.model.AddScheduleItemRequestModel
import com.unique.schedify.post_auth.schedule_list.domain.repository.ScheduleListRepository
import javax.inject.Inject

class AddScheduleListUseCase @Inject constructor(
    private val repository: ScheduleListRepository,
) : ApiUseCase<ApiResponseResource<AddScheduleItemDto>, AddScheduleItemRequestModel>{
    override suspend fun execute(args: AddScheduleItemRequestModel?): ApiResponseResource<AddScheduleItemDto> {
        val result = repository.addScheduleItem(args)
        return (if (result.isSuccessful) {
            result.body()?.let { data ->
                ApiResponseResource.Success(data)
            } ?: ApiResponseResource.Error("")
        } else {
            ApiResponseResource.Error(result.errorBody()?.string() ?: "Something went wrong with error body")
        })
    }
}