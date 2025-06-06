package com.unique.schedify.post_auth.schedule_list.use_case

import com.unique.schedify.core.ApiUseCase
import com.unique.schedify.core.util.ApiResponseResource
import com.unique.schedify.post_auth.schedule_list.data.remote.dto.VerifyPinCodeResponseDto
import com.unique.schedify.post_auth.schedule_list.domain.repository.ScheduleListRepository
import javax.inject.Inject

class VerifyPinCodeCase @Inject constructor(
    private val repository: ScheduleListRepository,
) : ApiUseCase<ApiResponseResource<VerifyPinCodeResponseDto>, String>{
    override suspend fun execute(args: String?): ApiResponseResource<VerifyPinCodeResponseDto> {
        val result = repository.verifyPinCode(args)
        return (if (result.isSuccessful) {
            result.body()?.let { data ->
                if (data.isNotEmpty() && data[0].status == "Error") {
                    ApiResponseResource.Error(data[0].message.toString())
                } else if (data.isNotEmpty()) {
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