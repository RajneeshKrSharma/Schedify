package com.unique.schedify.post_auth.schedule_list.use_case

import com.unique.schedify.core.ApiUseCase
import com.unique.schedify.core.util.ApiResponseResource
import com.unique.schedify.post_auth.schedule_list.data.remote.dto.AddAddressResponseDto
import com.unique.schedify.post_auth.schedule_list.data.remote.model.AddAddressRequestDto
import com.unique.schedify.post_auth.schedule_list.domain.repository.ScheduleListRepository
import javax.inject.Inject

class AddAddressCase @Inject constructor(
    private val repository: ScheduleListRepository,
) : ApiUseCase<ApiResponseResource<AddAddressResponseDto>, AddAddressRequestDto>{
    override suspend fun execute(args: AddAddressRequestDto?): ApiResponseResource<AddAddressResponseDto> {
        val result = repository.addAddress(args)
        return (if (result.isSuccessful) {
            result.body()?.let { data ->
                if (!data.address.isNullOrEmpty()) {
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