package com.unique.schedify.post_auth.schedule_list.domain.repository

import com.unique.schedify.post_auth.schedule_list.data.remote.dto.AddScheduleItemDto
import com.unique.schedify.post_auth.schedule_list.data.remote.dto.ScheduleListApis
import com.unique.schedify.post_auth.schedule_list.data.remote.dto.ScheduleListResponseDto
import com.unique.schedify.post_auth.schedule_list.data.remote.dto.VerifyPinCodeResponseDto
import com.unique.schedify.post_auth.schedule_list.data.remote.model.AddScheduleItemRequestModel
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
        return api.verifyPinCode(pinCode)
    }
}