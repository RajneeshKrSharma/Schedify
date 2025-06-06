package com.unique.schedify.post_auth.schedule_list.use_case

import com.unique.schedify.core.ApiUseCase
import com.unique.schedify.core.util.ApiResponseResource
import com.unique.schedify.post_auth.schedule_list.data.remote.dto.WeatherStatusResponseDto
import com.unique.schedify.post_auth.schedule_list.data.remote.model.GetWeatherStatusRequestDto
import com.unique.schedify.post_auth.schedule_list.domain.repository.ScheduleListRepository
import javax.inject.Inject

class GetWeatherStatusUseCase @Inject constructor(
    private val repository: ScheduleListRepository,
) : ApiUseCase<ApiResponseResource<WeatherStatusResponseDto>, GetWeatherStatusRequestDto>{
    override suspend fun execute(args: GetWeatherStatusRequestDto?): ApiResponseResource<WeatherStatusResponseDto> {
        val result = repository.getWeatherStatus(args?.pincode ?:"", args?.scheduleItemId ?: "")
        return (if (result.isSuccessful) {
            result.body()?.let { data ->
                if (data.weatherNotifyDetails != null) {
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