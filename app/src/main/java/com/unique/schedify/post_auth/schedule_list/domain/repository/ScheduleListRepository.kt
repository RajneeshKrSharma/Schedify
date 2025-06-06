package com.unique.schedify.post_auth.schedule_list.domain.repository

import com.unique.schedify.core.network.Api.PIN_CODE_VERIFY
import com.unique.schedify.post_auth.schedule_list.data.remote.dto.AddAddressResponseDto
import com.unique.schedify.post_auth.schedule_list.data.remote.dto.AddScheduleItemDto
import com.unique.schedify.post_auth.schedule_list.data.remote.dto.AttachedFileResponseDto
import com.unique.schedify.post_auth.schedule_list.data.remote.dto.ScheduleListApis
import com.unique.schedify.post_auth.schedule_list.data.remote.dto.ScheduleListResponseDto
import com.unique.schedify.post_auth.schedule_list.data.remote.dto.VerifyPinCodeResponseDto
import com.unique.schedify.post_auth.schedule_list.data.remote.dto.WeatherStatusResponseDto
import com.unique.schedify.post_auth.schedule_list.data.remote.model.AddAddressRequestDto
import com.unique.schedify.post_auth.schedule_list.data.remote.model.AddScheduleItemRequestModel
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class ScheduleListRepository @Inject constructor(
    private val api: ScheduleListApis
) {

    suspend fun getScheduleList(): Response<ScheduleListResponseDto> {
        return api.getScheduleList()
    }

    suspend fun addScheduleItem(req: AddScheduleItemRequestModel?): Response<AddScheduleItemDto> {
        return api.addScheduleItem(req)
    }

    suspend fun verifyPinCode(pinCode: String?): Response<VerifyPinCodeResponseDto> {
        return api.verifyPinCode("$PIN_CODE_VERIFY$pinCode")
    }

    suspend fun uploadFiles(scheduleId: String, files: List<MultipartBody.Part>): Response<AttachedFileResponseDto> {
        return api.uploadFiles(scheduleId.toInt(), files)
    }

    suspend fun addAddress(req: AddAddressRequestDto?): Response<AddAddressResponseDto> {
        return api.addAddress(req)
    }

    suspend fun getWeatherStatus(pinCode: String, scheduleItemId: String): Response<WeatherStatusResponseDto> {
        return api.getWeatherStatus(pinCode, scheduleItemId)
    }
}