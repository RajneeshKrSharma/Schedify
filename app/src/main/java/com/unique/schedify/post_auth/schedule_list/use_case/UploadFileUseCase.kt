package com.unique.schedify.post_auth.schedule_list.use_case

import com.unique.schedify.core.ApiUseCase
import com.unique.schedify.core.util.ApiResponseResource
import com.unique.schedify.post_auth.schedule_list.data.remote.dto.AttachedFileResponseDto
import com.unique.schedify.post_auth.schedule_list.data.remote.model.UploadFileRequestDto
import com.unique.schedify.post_auth.schedule_list.domain.repository.ScheduleListRepository
import javax.inject.Inject

class UploadFileUseCase @Inject constructor(
    private val repository: ScheduleListRepository,
) : ApiUseCase<ApiResponseResource<AttachedFileResponseDto>, UploadFileRequestDto>{
    override suspend fun execute(args: UploadFileRequestDto?): ApiResponseResource<AttachedFileResponseDto> {
        val result = repository.uploadFiles(args?.scheduleId ?: "", args?.files ?: emptyList())
        return (if (result.isSuccessful) {
            result.body()?.let { data ->
                if (!data.attachments.isNullOrEmpty()) {
                    ApiResponseResource.Success(data)
                } else {
                    ApiResponseResource.Error("Empty response data")
                }
            } ?: ApiResponseResource.Error("")
        } else {
            ApiResponseResource.Error(result.errorBody()?.string() ?: "Something went wrong with error body")
        })
    }
}